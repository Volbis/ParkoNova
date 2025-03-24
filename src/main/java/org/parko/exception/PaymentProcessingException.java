package org.parko.exception;

public class PaymentProcessingException extends Exception {
    public PaymentProcessingException(String message) {
        super("Erreur lors du traitement du paiement : " + message);
    }
}