package com.dhruv.employee_management.service;

import com.dhruv.employee_management.entity.Employee;
import com.dhruv.employee_management.repository.EmployeeRepository;
import com.dhruv.employee_management.specification.EmployeeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Service
public class IsolationService {

    @PersistenceContext
    private EntityManager entityManager;
    private final EmployeeRepository employeeRepository;

    public IsolationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transactionB() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        System.out.println(
                "Transaction B: salary = " + employee.getSalary()
        );
    }


   // @Transactional(isolation = Isolation.READ_COMMITTED)
   @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transactionBReadTwice() {

        Employee firstRead =
                employeeRepository.findById(1L).orElseThrow();

        System.out.println(
                "Transaction B - First read salary = "
                        + firstRead.getSalary()
        );

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        entityManager.clear();

        Employee secondRead =
                employeeRepository.findById(1L).orElseThrow();

        System.out.println(
                "Transaction B - Second read salary = "
                        + secondRead.getSalary()
        );
    }


   // @Transactional(isolation = Isolation.REPEATABLE_READ)
   @Transactional(isolation = Isolation.SERIALIZABLE)
   public void transactionBPhantomRead() {

       Specification<Employee> specification =
               EmployeeSpecification.hasMinimumSalary(100000.0);

       List<Employee> firstRead =
               employeeRepository.findAll(specification);

       System.out.println(
               "Transaction B - First read count = "
                       + firstRead.size()
       );

       System.out.println("Transaction B - Sleeping for 15 seconds...");

       try {
           Thread.sleep(15000);
       } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
       }

       System.out.println("Transaction B - Waking up");

       entityManager.clear();

       List<Employee> secondRead =
               employeeRepository.findAll(specification);

       System.out.println(
               "Transaction B - Second read count = "
                       + secondRead.size()
       );
   }


    @Transactional
    public void transactionAPhantomInsertAndCommit() {

        Employee employee = new Employee(
                null,
                "Phantom Employee 2",
                "phantom2@test.com",
                "IT",
                140000
        );

        employeeRepository.saveAndFlush(employee);

        System.out.println(
                "Transaction A - INSERT flushed, method is about to return"
        );
    }
}