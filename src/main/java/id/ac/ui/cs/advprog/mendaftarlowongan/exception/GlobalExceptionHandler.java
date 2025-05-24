package id.ac.ui.cs.advprog.mendaftarlowongan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LamaranNotFoundExceptionException.class)
    public ResponseEntity<String> handleLamaranNotFound(LamaranNotFoundExceptionException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(LowonganNotFoundExceptionException.class)
    public ResponseEntity<String> handleLowonganNotFound(LowonganNotFoundExceptionException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Terjadi kesalahan: " + ex.getMessage());
    }
}
