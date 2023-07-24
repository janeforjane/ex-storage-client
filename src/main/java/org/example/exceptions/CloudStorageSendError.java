package org.example.exceptions;

public class CloudStorageSendError extends Exception{

    public CloudStorageSendError(String message) {
        super(message);
    }

    public CloudStorageSendError(String message, Throwable e) {
        super(message, e);
    }
}
