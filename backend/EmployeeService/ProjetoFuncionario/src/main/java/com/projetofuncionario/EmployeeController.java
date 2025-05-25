package com.projetofuncionario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*") // permite acesso do Angular
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @GetMapping
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Employee> findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @PutMapping
    public Employee update(@RequestBody Employee employee) {
        return employeeService.update(employee);
    }

    @GetMapping("/active/{active}")
    public List<Employee> findByActive(@PathVariable boolean active) {
        return employeeService.findByActive(active);
    }

    @GetMapping("/email/{email}")
    public Optional<Employee> findByEmail(@PathVariable String email) {
        return employeeService.findByEmail(email);
    }
}
