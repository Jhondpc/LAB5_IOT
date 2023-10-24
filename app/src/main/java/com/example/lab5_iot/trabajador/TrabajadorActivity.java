package com.example.lab5_iot.trabajador;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityTrabajadorBinding;
import com.example.lab5_iot.entity.Employee;
import com.example.lab5_iot.entity.EmployeeDto;
import com.example.lab5_iot.service.EmployeRepository;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrabajadorActivity extends AppCompatActivity {
    ActivityTrabajadorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrabajadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String channelId = "canalTrabajador";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_notification_important_24)
                .setContentTitle("Está entrando en modo Empleado")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        //Se bloquea el botón de descargar horario
        binding.button.setOnClickListener(v -> {
            showAlertDialog("Primero debe ingresar su código de empleado y presionar el botón Buscar");
            binding.button.setEnabled(false);
        });

        binding.button2.setOnClickListener(v -> {
            TextInputLayout inputCodigo = binding.inputCodigo;
            String codigoStr = inputCodigo.getEditText().getText().toString();

            if (codigoStr.isEmpty()) {
                showAlertDialog("Debe ingresar su código.");
            } else {
                if (!isNumeric(codigoStr)) {
                    showAlertDialog("Su código debe ser un número.");
                } else {
                    int codigoTrabajador = Integer.parseInt(codigoStr);
                    EmployeRepository employeRepository = new Retrofit.Builder()
                            .baseUrl("http://192.168.0.2:8080")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(EmployeRepository.class);
                    employeRepository.obtenerTrabajador(codigoTrabajador).enqueue(new Callback<EmployeeDto>() {
                        @Override
                        public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                            if (response.isSuccessful()) {
                                Employee employee = response.body().getEmployee();
                                if (employee != null) {
                                    binding.textViewBienvenida.setText("Bienvenido "+employee.getFirstName()+" "+employee.getLastName());
                                    binding.textViewBienvenida.setVisibility(View.VISIBLE);
                                    binding.button.setEnabled(true);
                                    binding.button.setText("Descargar horarios de "+employee.getFirstName());
                                    if(employee.getMeeting()==1){
                                        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                                        SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm");
                                        String fechaCita = formatFecha.format(employee.getMeetingDate());
                                        String horaCita = formatHora.format(employee.getMeetingDate());

                                        String channelId = "canalTrabajador";
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(TrabajadorActivity.this, channelId)
                                                .setSmallIcon(R.drawable.baseline_notification_important_24)
                                                .setContentTitle("Usted tiene una tutoría agendada para "+fechaCita+" a las "+horaCita+" Hrs.")
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setAutoCancel(true);
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        notificationManager.notify(1, builder.build());

                                        // Obtener la fecha y hora actual
                                        LocalDateTime currentDateTime = LocalDateTime.now();

                                        // Convertir la fecha de la cita a LocalDateTime
                                        Date meetingDate = employee.getMeetingDate();
                                        LocalDateTime meetingDateTime = meetingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                        if (meetingDateTime.isBefore(currentDateTime)) {
                                            binding.textView7.setText("Puede envíar el feedback de su cita realizada el "+fechaCita+" . El mensaje debe tener 250 caracteres como máximo.");
                                            binding.textView7.setVisibility(View.VISIBLE);
                                            binding.inputFeedback.setVisibility(View.VISIBLE);
                                            binding.button3.setVisibility(View.VISIBLE);

                                            binding.button3.setOnClickListener(v2 ->{
                                                TextInputLayout inputFeedback = binding.inputFeedback;
                                                String feedback = inputFeedback.getEditText().getText().toString();
                                                if(feedback.isEmpty()){
                                                    showAlertDialog("Debe ingresar un feedback.");
                                                }else{
                                                    employeRepository.insertarFeedback(employee.getEmployeeId(), feedback);
                                                    NotificationCompat.Builder builder1 = new NotificationCompat.Builder(TrabajadorActivity.this, channelId)
                                                            .setSmallIcon(R.drawable.baseline_notification_important_24)
                                                            .setContentTitle("Feedback enviado de manera exitosa.")
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setAutoCancel(true);
                                                    NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    notificationManager1.notify(1, builder1.build());
                                                }
                                            });
                                        }
                                    }
                                    binding.button.setOnClickListener(v1 -> {
                                        if(employee.getMeeting()==1){
                                            descargarImagenHorario();
                                        }else{
                                            String channelId = "canalTrabajador";
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(TrabajadorActivity.this, channelId)
                                                    .setSmallIcon(R.drawable.baseline_notification_important_24)
                                                    .setContentTitle("No cuenta con tutorías pendientes")
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setAutoCancel(true);
                                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManager.notify(1, builder.build());
                                        }
                                    });

                                } else {
                                    showAlertDialog("No se encontró un trabajador con el código ingresado.");
                                }
                            } else {
                                Log.d("msg-test", "Error en la respuesta del webservice");
                            }
                        }
                        @Override
                        public void onFailure(Call<EmployeeDto> call, Throwable t) {
                            Log.d("msg-test", "Error en la solicitud al webservice: " + t.getMessage());
                        }
                    });
                }
            }
        });

        binding.button.setOnClickListener(v -> {

        });
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Aviso")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void descargarImagenHorario() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        // >29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            String fileName = "horarios.jpg";
            String endPoint = "https://i.pinimg.com/564x/4e/8e/a5/4e8ea537c896aa277e6449bdca6c45da.jpg";

            Uri downloadUri = Uri.parse(endPoint);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle(fileName);
            request.setMimeType("image/jpeg");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

            showAlertDialog("Se descargó exitosamente los horarios.");
        } else {
            launcher.launch(permission);
        }
    }
    ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    descargarImagenHorario();
                } else {
                    Log.e("msg-test", "Permiso denegado");
                }
            });
}