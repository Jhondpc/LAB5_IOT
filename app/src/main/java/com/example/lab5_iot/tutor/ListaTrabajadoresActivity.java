package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityListaTrabajadoresBinding;
import com.example.lab5_iot.entity.Employee;
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

public class ListaTrabajadoresActivity extends AppCompatActivity {
    ActivityListaTrabajadoresBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTrabajadoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");

        binding.button.setOnClickListener(v -> {
            TextInputLayout inputCodigo = binding.inputCodigo;
            String codigoStr = inputCodigo.getEditText().getText().toString();

            if (codigoStr.isEmpty()) {
                showAlertDialog("Debe ingresar el código del tutor.");
            } else {
                if (!isNumeric(codigoStr)) {
                    showAlertDialog("Debe ingresar un número como código de tutor.");
                } else {
                    int codigoTutor = Integer.parseInt(codigoStr);
                    EmployeRepository employeRepository = new Retrofit.Builder()
                            .baseUrl("http://"+ip+":8080")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(EmployeRepository.class);
                    employeRepository.obtenerListaTrabajadores(codigoTutor).enqueue(new Callback<List<Employee>>() {
                        @Override
                        public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                            if (response.isSuccessful()) {
                                List<Employee> employeeList = response.body();
                                if (employeeList != null && !employeeList.isEmpty()) {
                                    guardarListaTrabajadores(employeeList);
                                } else {
                                    showAlertDialog("No se encontraron trabajadores para el código de tutor ingresado.");
                                }
                            } else {
                                Log.d("msg-test", "Error en la respuesta del webservice");
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Employee>> call, Throwable t) {
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