package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public class SKSInvalidException extends RuntimeException {
    public SKSInvalidException() {
        super("SKS tidak valid, harus diantara 0 dan 24");
    }
}
