package com.dhruv.employee_management.dto;

import com.dhruv.employee_management.entity.Employee;

public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private double salary;

    public EmployeeResponse(
            Long id,
            String name,
            String email,
            String department,
            double salary
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }


    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary()
        );
    }
}