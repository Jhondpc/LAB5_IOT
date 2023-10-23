package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityAsignarTutoriaBinding;

public class AsignarTutoriaActivity extends AppCompatActivity {

    ActivityAsignarTutoriaBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAsignarTutoriaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}