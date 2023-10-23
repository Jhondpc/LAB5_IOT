package com.example.lab5_iot.tutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lab5_iot.R;
import com.example.lab5_iot.databinding.ActivityListaTrabajadoresBinding;

public class ListaTrabajadoresActivity extends AppCompatActivity {

    ActivityListaTrabajadoresBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTrabajadoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}