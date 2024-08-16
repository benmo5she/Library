package com.storage.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Loan not found with ID: " + id);
    }
}