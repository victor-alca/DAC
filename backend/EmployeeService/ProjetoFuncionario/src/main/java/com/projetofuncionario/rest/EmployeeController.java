package com.projetofuncionario.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.projetofuncionario.dto.EmployeeRequestDTO;
import com.projetofuncionario.dto.EmployeeResponseDTO;
import com.projetofuncionario.model.Employee;
import com.projetofuncionario.service.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> save(@RequestBody EmployeeRequestDTO dto) {
        try {
            // Verifica se já existe funcionário com mesmo email ou cpf
            boolean existsEmail = employeeService.findByEmail(dto.getEmail()).isPresent();
            boolean existsCpf = employeeService.findAll().stream().anyMatch(e -> e.getCpf().equals(dto.getCpf()));
            if (existsEmail || existsCpf) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Funcionário já cadastrado");
            }
            Employee employee = toEntity(dto);
            Employee saved = employeeService.save(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(saved));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar funcionário");
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> findAll() {
        List<EmployeeResponseDTO> list = employeeService.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(list);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeResponseDTO> findByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeService.findByEmail(email);
        if (employee.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado");
        }
        return ResponseEntity.ok(toResponseDTO(employee.get()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> findById(@PathVariable Long id) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
        return ResponseEntity.ok(toResponseDTO(employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> delete(@PathVariable Long id) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
        employeeService.delete(id);
        return ResponseEntity.ok(toResponseDTO(employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id, @RequestBody EmployeeRequestDTO dto) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
        employee.setCpf(dto.getCpf());
        employee.setEmail(dto.getEmail());
        employee.setName(dto.getNome());
        employee.setPhone(dto.getTelefone());
        Employee updated = employeeService.update(employee);
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    // Métodos utilitários para conversão
    private Employee toEntity(EmployeeRequestDTO dto) {
        Employee employee = new Employee();
        employee.setCpf(dto.getCpf());
        employee.setEmail(dto.getEmail());
        employee.setName(dto.getNome());
        employee.setPhone(dto.getTelefone());
        employee.setActive(true);
        return employee;
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setCodigo(employee.getId());
        dto.setCpf(employee.getCpf());
        dto.setEmail(employee.getEmail());
        dto.setNome(employee.getName());
        dto.setTelefone(employee.getPhone());
        dto.setTipo("FUNCIONARIO");
        return dto;
    }
}