package br.com.marejoias.customer.exception;

public class DuplicateCpfException extends RuntimeException {
    public DuplicateCpfException() {
        super("CPF já está em uso");
    }
}