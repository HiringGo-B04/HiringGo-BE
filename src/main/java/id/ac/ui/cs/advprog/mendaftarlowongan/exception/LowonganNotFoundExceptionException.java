package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public class LowonganNotFoundExceptionException extends ObjectNotFoundException {
    public LowonganNotFoundExceptionException(String id) {
        super("Lowongan dengan ID " + id + " tidak ditemukan.");
    }
}