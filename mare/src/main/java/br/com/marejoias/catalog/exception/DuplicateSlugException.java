package br.com.marejoias.catalog.exception;

public class DuplicateSlugException extends RuntimeException {
    public DuplicateSlugException() {
        super("Slug já está em uso");
    }
}
