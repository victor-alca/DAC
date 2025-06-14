package com.projetofuncionario.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.message.SagaMessage;
import com.projetofuncionario.model.Employee;

@Component
public class ConsumerCompensateEmployee {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeService employeeService;

    @RabbitListener(queues = "funcionario.employee.compensar")
    public void receiveRead(@Payload String json) {
        try {
            SagaMessage<Employee> message = objectMapper.readValue(json, new TypeReference<SagaMessage<Employee>>() {});
            Employee employee = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[EMPLOYEE] Executando rollback para SAGA " + correlationId);

                Employee employeeToDelete = employeeService.findByCpf(employee.getCpf());
                if (employeeToDelete != null) {
                    employeeService.delete(employeeToDelete.getId());
                    System.out.println("[EMPLOYEE] Funcionario com CPF " + employee.getCpf() + " deletado com sucesso.");
                } else {
                    System.out.println("[EMPLOYEE] Nenhum cliente encontrado com CPF " + employee.getCpf());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
