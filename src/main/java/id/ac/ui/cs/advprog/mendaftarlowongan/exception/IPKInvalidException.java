package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public class IPKInvalidException extends RuntimeException {
    public IPKInvalidException() {
        super("IPK tidak valid, harus diantara 0.0 dan 4.0");
    }
}
