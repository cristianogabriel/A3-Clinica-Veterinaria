package com.clinica.ibmr.api;

public final class ApiExceptions {
    private ApiExceptions() {}
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }
    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) { super(message); }
    }
    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String message) { super(message); }
    }
}
