package com.example.lab5_iot.service;

import com.example.lab5_iot.entity.Employee;
import com.example.lab5_iot.entity.EmployeeDto;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EmployeRepository {

    @GET("/tutor/listaTrabajadores")
    Call<List<Employee>> obtenerListaTrabajadores(@Query("managerId") Integer managerId);

    @GET("/tutor/trabajador")
    Call<EmployeeDto> obtenerTrabajador(@Query("employeeId") Integer id);

    @FormUrlEncoded
    @POST("/tutor/asignarTutoria")
    Call<Integer> asignarTutoria(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("/tutor/insertarFeedback")
    Call<HashMap<String, String>> insertarFeedback(@Field("employeeId") Integer employeeId, @Field("feedback") String feedback);

}
