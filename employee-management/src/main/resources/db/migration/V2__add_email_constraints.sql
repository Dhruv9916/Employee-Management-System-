ALTER TABLE employees
    MODIFY COLUMN email VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_employees_email UNIQUE (email);