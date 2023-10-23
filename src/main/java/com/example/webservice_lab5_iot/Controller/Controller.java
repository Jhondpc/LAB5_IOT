package com.example.webservice_lab5_iot.Controller;

import com.example.webservice_lab5_iot.Entity.Employee;
import com.example.webservice_lab5_iot.Repository.EmployeeRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class Controller {

    @Autowired
    EmployeeRepository employeeRepository;

    //Listar trabajadores por manager
    @GetMapping("/tutor/listaTrabajadores")
    public List<Employee> listarTrabajadores(@RequestParam("managerId") int managerId){
        return employeeRepository.listarTrabajadoresPorManager(managerId);
    }

    //Buscar trabajador por id
    @GetMapping("/tutor/trabajador")
    public ResponseEntity<HashMap<String,Object>> obtenerTrabajador(@RequestParam("employeeId") int employeeId){
        HashMap<String,Object> hashMap = new HashMap<>();
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if(optionalEmployee.isPresent()){
            hashMap.put("employee", optionalEmployee.get());
        }else {
            hashMap.put("msg","No existe un trabajador con ese ID");
        }
        return ResponseEntity.ok(hashMap);
    }

}
