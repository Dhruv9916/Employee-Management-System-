package com.dhruv.employee_management.controller;

import com.dhruv.employee_management.dto.EmployeeRequest;
import com.dhruv.employee_management.dto.EmployeeResponse;
import com.dhruv.employee_management.entity.Employee;
import com.dhruv.employee_management.service.EmployeeService;
import com.dhruv.employee_management.service.IsolationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;
    private final IsolationService isolationService;

    public EmployeeController(EmployeeService employeeService, IsolationService isolationService) {
        this.employeeService = employeeService;
        this.isolationService = isolationService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false)
            @PositiveOrZero(message = "minSalary must be greater than or equal to 0")
            Double minSalary,
            @RequestParam(required = false)
            @PositiveOrZero(message = "maxSalary must be greater than or equal to 0")
            Double maxSalary,
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                employeeService.getAllEmployees(
                        department,
                        minSalary,
                        maxSalary,
                        name,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                employeeService.getEmployeeById(id)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request
    ) {
        return ResponseEntity.ok(
                employeeService.updateEmployee(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id
    ) {
        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }


//    @PostMapping("/test-transaction")
//    public ResponseEntity<String> testTransaction() {
//
//        employeeService.testTransaction();
//
//        return ResponseEntity.ok("Transaction completed");
//    }


    @PostMapping("/test-transaction")
    public ResponseEntity<String> testTransaction() throws Exception {

        employeeService.testTransaction();

        return ResponseEntity.ok("Transaction completed");
    }


    @PostMapping("/test-propagation")
    public ResponseEntity<String> testPropagation() {

        employeeService.operationA();

        return ResponseEntity.ok("Propagation completed");
    }


    @PostMapping("/test-isolation")
    public ResponseEntity<String> testIsolation() {
        employeeService.transactionA();
        return ResponseEntity.ok("Transaction completed");
    }

    @GetMapping("/test-isolation/read")
    public ResponseEntity<String> readDuringTransaction() {

        isolationService.transactionB();

        return ResponseEntity.ok("Read completed");
    }


    @PostMapping("/read-twice")
    public String readTwice() {
        isolationService.transactionBReadTwice();
        return "Transaction B completed";
    }

    @PostMapping("/update-commit")
    public String updateAndCommit() {
        employeeService.transactionAUpdateAndCommit();
        return "Transaction A committed";
    }

    @PostMapping("/transaction-test/phantom-read")
    public String phantomRead() {
        isolationService.transactionBPhantomRead();
        return "Transaction B completed";
    }

    @PostMapping("/transaction-test/phantom-insert")
    public String phantomInsert() {
        isolationService.transactionAPhantomInsertAndCommit();
        return "Transaction A committed";
    }


}