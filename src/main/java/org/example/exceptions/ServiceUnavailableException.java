package org.example.exceptions;

public class ServiceUnavailableException extends Exception{

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable e) {
        super(message, e);
    }
}
