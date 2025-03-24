package org.parko.exception;

public class ParkingFullException extends Exception {
    public ParkingFullException() {
        super("Le parking est complet. Aucune place disponible.");
    }
}