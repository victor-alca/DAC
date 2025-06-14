package com.projetofuncionario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetofuncionario.model.Employee;
import com.projetofuncionario.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

  @Autowired
  private EmployeeRepository employeeRepository;

  public Employee save(Employee employee) {
    return employeeRepository.save(employee);
  }

  public Employee update(Employee employee) {
    return employeeRepository.save(employee);
  }

  public void delete(Long id) {
    employeeRepository.deleteById(id);
  }

  public Optional<Employee> findById(Long id) {
    return employeeRepository.findById(id);
  }

  public List<Employee> findAll() {
    return employeeRepository.findAll();
  }

  public List<Employee> findByActive(boolean active) {
    return employeeRepository.findByActive(active);
  }

  public Optional<Employee> findByEmail(String email) {
    return employeeRepository.findByEmail(email);
  }

  public Optional<Employee> findByCpf(String cpf) {
    return employeeRepository.findByCpf(cpf);
  }
}
