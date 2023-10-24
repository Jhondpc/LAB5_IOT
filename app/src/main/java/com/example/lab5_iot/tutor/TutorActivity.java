package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityTutorBinding;

public class TutorActivity extends AppCompatActivity {
    ActivityTutorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");

        String channelId = "canalTutor";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_notification_important_24)
                .setContentTitle("Está entrando en modo Tutor")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        binding.btnDescargarListaTrabajadores.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ListaTrabajadoresActivity.class);
            intent1.putExtra("ip",ip);
            startActivity(intent1);
        });

        binding.btnBuscarTrabajador.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, BuscarTrabajadorActivity.class);
            intent1.putExtra("ip",ip);
            startActivity(intent1);
        });

        binding.btnAsignarTutoria.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, AsignarTutoriaActivity.class);
            intent1.putExtra("ip",ip);
            startActivity(intent1);
        });
    }
}