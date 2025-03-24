package org.parko.exception;

public class TarifCalculationException extends Exception {
    public TarifCalculationException(String message) {
        super("Erreur lors du calcul du tarif : " + message);
    }
}