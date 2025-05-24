package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public class DuplicateLamaranException extends RuntimeException {
    public DuplicateLamaranException() {
        super("Lamaran sudah ada");
    }
}
