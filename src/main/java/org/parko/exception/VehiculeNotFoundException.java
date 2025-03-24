package org.parko.exception;

public class VehiculeNotFoundException extends Exception {
    public VehiculeNotFoundException(String immatriculation) {
        super("Véhicule avec l'immatriculation " + immatriculation + " non trouvé.");
    }
}