package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

public class LamaranNotFoundExceptionException extends ObjectNotFoundException {
    public LamaranNotFoundExceptionException(String id) {
        super("Lamaran dengan ID " + id + " tidak ditemukan.");
    }
}
