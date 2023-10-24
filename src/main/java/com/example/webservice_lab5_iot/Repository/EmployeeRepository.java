package com.example.webservice_lab5_iot.Repository;

import com.example.webservice_lab5_iot.Entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    //Listar Trabajadores por manager
    @Query(value = "Select * from hr_v2.employees where manager_id=?1", nativeQuery = true)
    List<Employee> listarTrabajadoresPorManager(int managerId);

    //Asignar cita a trabajador
    @Transactional
    @Modifying
    @Query(value = "UPDATE hr_v2.employees SET meeting = 1, meeting_date = NOW() WHERE employee_id = ?1", nativeQuery = true)
    void asignarCita(int employeeId);

}
