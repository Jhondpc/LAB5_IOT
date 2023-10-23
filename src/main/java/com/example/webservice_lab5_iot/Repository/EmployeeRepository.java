package com.example.webservice_lab5_iot.Repository;

import com.example.webservice_lab5_iot.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query(value = "Select * from hr_v2.employees where manager_id=?1", nativeQuery = true)
    List<Employee> listarTrabajadoresPorManager(int managerId);
}
