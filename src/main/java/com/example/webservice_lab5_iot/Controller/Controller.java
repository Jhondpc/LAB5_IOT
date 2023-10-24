package com.example.webservice_lab5_iot.Controller;

import com.example.webservice_lab5_iot.Entity.Employee;
import com.example.webservice_lab5_iot.Repository.EmployeeRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/tutor/asignarTutoria",consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<HashMap<String,String>> asignarTutoria(Integer id){
        HashMap<String,String> hashMap = new HashMap<>();
        Optional<Employee> optionalProduct = employeeRepository.findById(id);
        if(optionalProduct.isPresent()){
            Employee employee = optionalProduct.get();
            employeeRepository.asignarCita(employee.getEmployeeId());
            hashMap.put("status", "Empleado actualizado");
            return ResponseEntity.status(HttpStatus.CREATED).body(hashMap);
        }else{
            hashMap.put("status","Error");
            hashMap.put("msg","El empleado no se encontró en la base de datos o no existe");
            return ResponseEntity.ok(hashMap);
        }
    }

    @PostMapping(value = "/tutor/insertarFeedback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> insertarFeedback(
            @RequestParam("employeeId") int employeeId,
            @RequestParam("feedback") String feedback) {
        HashMap<String, String> hashMap = new HashMap<>();

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setEmployeeFeedback(feedback); // Actualizar el campo de feedback
            employeeRepository.save(employee); // Guardar los cambios en la base de datos

            hashMap.put("status", "Feedback insertado");
            return ResponseEntity.status(HttpStatus.CREATED).body(hashMap);
        } else {
            hashMap.put("status", "Error");
            hashMap.put("msg", "El empleado no se encontró en la base de datos o no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(hashMap);
        }
    }


}
