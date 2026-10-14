package br.com.marejoias.customer.exception;

public class MaxAddressesReachedException extends RuntimeException {
    public MaxAddressesReachedException() {
        super("Limite máximo de endereços atingido");
    }
}