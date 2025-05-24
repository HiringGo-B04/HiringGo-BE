package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public abstract class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
