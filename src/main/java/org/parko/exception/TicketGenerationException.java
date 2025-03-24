package org.parko.exception;

public class TicketGenerationException extends Exception {
    public TicketGenerationException(String message) {
        super("Erreur lors de la génération du ticket : " + message);
    }
}
