package org.parko.exception;

public class PlaceNotFoundException extends Exception {
    public PlaceNotFoundException(int placeId) {
        super("Place avec l'ID " + placeId + " non trouvée.");
    }
}