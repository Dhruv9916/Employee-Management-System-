package com.dhruv.employee_management.service;

import com.dhruv.employee_management.dto.EmployeeRequest;
import com.dhruv.employee_management.dto.EmployeeResponse;
import com.dhruv.employee_management.entity.Employee;
import com.dhruv.employee_management.exception.EmployeeNotFoundException;
import com.dhruv.employee_management.mapper.EmployeeMapper;
import com.dhruv.employee_management.repository.EmployeeRepository;
import com.dhruv.employee_management.specification.EmployeeSpecification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PropagationService propagationService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper,
            PropagationService propagationService
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.propagationService = propagationService;
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    public Page<EmployeeResponse> getAllEmployees(
            String department,
            Double minSalary,
            Double maxSalary,
            String name,
            Pageable pageable
    ) {



        if (minSalary != null
                && maxSalary != null
                && minSalary > maxSalary) {

            throw new IllegalArgumentException(
                    "minSalary cannot be greater than maxSalary"
            );
        }


        Specification<Employee> specification = null;

        // department specification
        if (department != null && !department.isBlank()) {
            specification = EmployeeSpecification.hasDepartment(department);
        }

        // minSalary specification
        if (minSalary != null) {
            Specification<Employee> salarySpecification =
                    EmployeeSpecification.hasMinimumSalary(minSalary);

            specification = specification == null
                    ? salarySpecification
                    : specification.and(salarySpecification);
        }

        // maxSalary specification
        if (maxSalary != null) {
            Specification<Employee> salarySpecification =
                    EmployeeSpecification.hasMaximumSalary(maxSalary);

            specification = specification == null
                    ? salarySpecification
                    : specification.and(salarySpecification);
        }

        // name specification
        if (name != null && !name.isBlank()) {
            Specification<Employee> nameSpecification =
                    EmployeeSpecification.hasName(name);

            specification = specification == null
                    ? nameSpecification
                    : specification.and(nameSpecification);
        }

        Page<Employee> employees =
                employeeRepository.findAll(specification, pageable);

        return employees.map(employeeMapper::toResponse);
    }

    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        return employeeMapper.toResponse(employee);
    }


    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request
    ) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        employeeMapper.updateEntity(existingEmployee, request);

        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        return employeeMapper.toResponse(updatedEmployee);
    }


    public void deleteEmployee(Long id) {

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + id
                        )
                );

        employeeRepository.delete(existingEmployee);
    }

//   //Add a transactional test operation  for unchecked exp.
//    @Transactional
//    public void testTransaction() {
//
//        Employee employee1 = new Employee(
//                null,
//                "Transaction One",
//                "transaction1@test.com",
//                "IT",
//                50000
//        );
//
//        employeeRepository.save(employee1);
//
//        Employee employee2 = new Employee(
//                null,
//                "Transaction Two",
//                "transaction2@test.com",
//                "IT",
//                60000
//        );
//
//        employeeRepository.save(employee2);
//
//    }

   //Add a transactional test operation  for checked exp.

    @Transactional(rollbackFor = Exception.class)
    public void testTransaction() throws Exception {

        Employee employee1 = new Employee(
                null,
                "Transaction Checked One",
                "checkedd1@test.com",
                "IT",
                50000
        );

        employeeRepository.save(employee1);

        Employee employee2 = new Employee(
                null,
                "Transaction Checked Twoo",
                "checkedd" +
                        "2@test.com",
                "IT",
                60000
        );

        employeeRepository.save(employee2);

        throw new Exception("Checked exception occurred");
    }


//    @Transactional
//    public void operationA() {
//
//        Employee employee1 = new Employee(
//                null,
//                "Propagation One",
//                "propagation1@test.com",
//                "IT",
//                50000
//        );
//
//        employeeRepository.save(employee1);
//
//        propagationService.operationB();
//    }


    @Transactional
    public void operationA() {

        Employee employee1 = new Employee(
                null,
                "Propagation One",
                "propagation1@test.com",
                "IT",
                50000
        );

        employeeRepository.save(employee1);

        try {
            propagationService.operationB();
        } catch (RuntimeException exception) {
            System.out.println("Operation B failed: " + exception.getMessage());
        }
    }


    @Transactional
    public void transactionA() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        employee.setSalary(100000);

        employeeRepository.save(employee);
        employeeRepository.flush();

        System.out.println("Transaction A: salary changed to 100000");

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        throw new RuntimeException("Transaction A rolling back");
    }


    @Transactional
    public void transactionAUpdateAndCommit() {

        Employee employee =
                employeeRepository.findById(1L).orElseThrow();

        employee.setSalary(100000);

        employeeRepository.save(employee);
        employeeRepository.flush();

        System.out.println(
                "Transaction A: salary changed to 100000 and will commit"
        );
    }

}