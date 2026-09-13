package com.dhruv.employee_management.service;

import com.dhruv.employee_management.entity.Employee;
import com.dhruv.employee_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationService {

    private final EmployeeRepository employeeRepository;

    public PropagationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

//    @Transactional
//    public void operationB() {
//
//        Employee employee2 = new Employee(
//                null,
//                "Propagation Two",
//                "propagation2@test.com",
//                "IT",
//                60000
//        );
//
//        employeeRepository.save(employee2);
//
//        throw new RuntimeException("Operation B failed");
//    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void operationB() {

        Employee employee2 = new Employee(
                null,
                "Propagation Two",
                "propagation2@test.com",
                "IT",
                60000
        );

        employeeRepository.save(employee2);

        throw new RuntimeException("Operation B failed");
    }
}