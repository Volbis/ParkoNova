package org.parko.exception;

public class InvalidVehicleTypeException extends Exception {
    public InvalidVehicleTypeException(String type) {
        super("Type de véhicule invalide : " + type);
    }
}