package com.example.lab5_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lab5_iot.databinding.ActivityMainBinding;
import com.example.lab5_iot.trabajador.TrabajadorActivity;
import com.example.lab5_iot.tutor.TutorActivity;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String ip = "10.100.253.19";   //CAMBIAR LA IP DE SU PC EN ESTE CAMPO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toast.makeText(this, "Recuerde cambiar su ip en MainActivity", Toast.LENGTH_SHORT).show();

        binding.buttonTutor.setOnClickListener(view -> {
            Intent intent = new Intent(this, TutorActivity.class);
            intent.putExtra("ip",ip);
            startActivity(intent);
        });

        binding.buttonTrabajador.setOnClickListener(view -> {
            Intent intent = new Intent(this, TrabajadorActivity.class);
            intent.putExtra("ip",ip);
            startActivity(intent);
        });

        crearCanalNotificacionTutor();
        crearCanalNotificacionTrabajador();
        askPermission();
    }

    private void crearCanalNotificacionTutor(){
        String channelId = "canalTutor";
        String channelName = "Canal Tutor";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void crearCanalNotificacionTrabajador(){
        String channelId = "canalTrabajador";
        String channelName = "Canal Trabajador";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void askPermission(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }
    }
}