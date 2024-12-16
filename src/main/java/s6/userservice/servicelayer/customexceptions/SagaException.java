package s6.userservice.servicelayer.customexceptions;

public class SagaException extends RuntimeException {
    public SagaException(String message, Exception e) {
        super(message, e);
    }
}
