package br.com.marejoias.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(DuplicateCpfException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateCpf(DuplicateCpfException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MaxAddressesReachedException.class)
    public ResponseEntity<Map<String, String>> handleMaxAddresses(MaxAddressesReachedException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", ex.getMessage()));
    }
}