package com.orchestrator.orchestrator.enums;

public enum EmployeeSagaStep {
    
    CREATE_EMPLOYEE,
    CPF_VALIDATION,
    CONFIRM_EMPLOYEE,
    COMPLETED,

    //Compensação
    ROLLBACK_EMPLOYEE,
    FAILED
}
