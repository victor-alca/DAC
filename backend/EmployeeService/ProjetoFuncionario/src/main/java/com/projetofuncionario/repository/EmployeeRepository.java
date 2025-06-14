package com.projetofuncionario.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetofuncionario.model.Employee;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCpf(String cpf);
    Optional<Employee> findByEmail(String email);
    List<Employee> findByActive(boolean active);
}
