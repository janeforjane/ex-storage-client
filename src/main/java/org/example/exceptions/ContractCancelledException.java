package org.example.exceptions;

public class ContractCancelledException extends Exception {

    public ContractCancelledException(String message) {
        super(message);
    }

    public ContractCancelledException(String message, Throwable e) {
        super(message, e);
    }
}
