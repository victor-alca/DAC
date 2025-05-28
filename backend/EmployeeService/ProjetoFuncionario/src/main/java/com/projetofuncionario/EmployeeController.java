package com.projetofuncionario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*") 
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // salvar
    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    // buscar todos
    @GetMapping
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    // buscar por id
    @GetMapping("/{id}")
    public Optional<Employee> findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    // excluir
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }

    // atualizar
    @PutMapping
    public Employee update(@RequestBody Employee employee) {
        return employeeService.update(employee);
    }

    // buscar ativos
    @GetMapping("/active/{active}")
    public List<Employee> findByActive(@PathVariable boolean active) {
        return employeeService.findByActive(active);
    }

    // buscar por email
    @GetMapping("/email/{email}")
    public Optional<Employee> findByEmail(@PathVariable String email) {
        return employeeService.findByEmail(email);
    }
}
