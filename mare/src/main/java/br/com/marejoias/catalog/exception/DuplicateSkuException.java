package br.com.marejoias.catalog.exception;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException() {
        super("SKU já está em uso");
    }
}
