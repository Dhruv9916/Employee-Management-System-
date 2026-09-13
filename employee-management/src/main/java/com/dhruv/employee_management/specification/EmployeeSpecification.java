package com.dhruv.employee_management.specification;

import com.dhruv.employee_management.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> hasDepartment(String department) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("department"),
                        department
                );
    }

    public static Specification<Employee> hasMinimumSalary(Double minSalary) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("salary"),
                        minSalary
                );
    }


    public static Specification<Employee> hasMaximumSalary(
            Double maxSalary
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("salary"),
                        maxSalary
                );
    }

    public static Specification<Employee> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}