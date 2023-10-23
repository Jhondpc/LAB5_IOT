package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityDescargaListaTrabajadoresBinding;

public class DescargaListaTrabajadoresActivity extends AppCompatActivity {
    ActivityDescargaListaTrabajadoresBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDescargaListaTrabajadoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}