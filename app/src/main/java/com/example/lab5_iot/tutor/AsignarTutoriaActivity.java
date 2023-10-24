package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityAsignarTutoriaBinding;
import com.example.lab5_iot.entity.Employee;
import com.example.lab5_iot.entity.EmployeeDto;
import com.example.lab5_iot.service.EmployeRepository;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AsignarTutoriaActivity extends AppCompatActivity {

    ActivityAsignarTutoriaBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAsignarTutoriaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");

        binding.button.setOnClickListener(v -> {
            TextInputLayout inputCodigoTutor = binding.inputCodigoTutor;
            String codigoTutorStr = inputCodigoTutor.getEditText().getText().toString();
            TextInputLayout inputCodigoEmpleado = binding.inputCodigoEmpleado;
            String codigoEmpleadoStr = inputCodigoEmpleado.getEditText().getText().toString();

            if (codigoTutorStr.isEmpty() || codigoEmpleadoStr.isEmpty()) {
                showAlertDialog("Se deben ingresar todos los campos obligatoriamente.");
            } else {
                if (!isNumeric(codigoTutorStr) || !isNumeric(codigoEmpleadoStr)) {
                    showAlertDialog("Todos los códigos que se ingresen deben ser números.");
                } else {
                    int codigoTutor = Integer.parseInt(codigoTutorStr);
                    int codigoEmpleado = Integer.parseInt(codigoEmpleadoStr);
                    EmployeRepository employeRepository = new Retrofit.Builder()
                            .baseUrl("http://"+ip+":8080")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(EmployeRepository.class);
                    employeRepository.obtenerTrabajador(codigoEmpleado).enqueue(new Callback<EmployeeDto>() {
                        @Override
                        public void onResponse(Call<EmployeeDto> call, Response<EmployeeDto> response) {
                            if (response.isSuccessful()) {
                                Employee employee = response.body().getEmployee();
                                if (employee != null) {
                                    if (employee.getManagerId() != null){
                                        if(employee.getManagerId() == codigoTutor){
                                            if(employee.getMeeting() == 1){     //En este caso el trabajador ya tiene una cita asignada
                                                //Se genera la notificación correspondiente
                                                String channelId = "canalTutor";
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(AsignarTutoriaActivity.this, channelId)
                                                        .setSmallIcon(R.drawable.baseline_notification_important_24)
                                                        .setContentTitle("“El trabajador ya tiene una cita asignada. Elija otro trabajador.")
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                        .setAutoCancel(true);
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.notify(1, builder.build());

                                            }else{      //En este caso se asigna una cita al trabajador
                                                employeRepository.asignarTutoria(codigoEmpleado).enqueue(new Callback<Integer>() {
                                                    @Override
                                                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                        Integer body = response.body();
                                                        Log.d("msg-test","WAAA: "+body);
                                                    }
                                                    @Override
                                                    public void onFailure(Call<Integer> call, Throwable t) {
                                                        t.printStackTrace();
                                                    }
                                                });
                                                String channelId = "canalTutor";
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(AsignarTutoriaActivity.this, channelId)
                                                        .setSmallIcon(R.drawable.baseline_notification_important_24)
                                                        .setContentTitle("Asignación del trabajador correcta.")
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                        .setAutoCancel(true);
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.notify(1, builder.build());
                                            }

                                        }else {
                                            showAlertDialog("El tutor ingresado no es tutor del trabajador ingresado.");
                                        }
                                    }else{
                                        showAlertDialog("El trabajador ingresado no cuenta con un manager.");
                                    }
                                } else {
                                    showAlertDialog("No se encontró a un trabajador con el código ingresado.");
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
    public void guardarListaTrabajadores(List<Employee> employeeList){
        Gson gson = new Gson();
        String lista = gson.toJson(employeeList);

        String fileName = "listaDeTrabajadores.txt";

        try (FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
             FileWriter fileWriter = new FileWriter(fileOutputStream.getFD())) {
            fileWriter.write(lista);
            Log.d("msg-test", "Se guardó el archivo exitosamente");
            String channelId = "canalTutor";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.baseline_notification_important_24)
                    .setContentTitle("Se ha descargado la lista de trabajadores exitosamente.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}