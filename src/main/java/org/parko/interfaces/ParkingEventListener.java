package org.parko.interfaces;

/**
 * Interface pour les écouteurs d'événements liés au parking.
 * Permet de réagir aux entrées et sorties de véhicules.
 */
public interface ParkingEventListener {
    /**
     * Méthode appelée lorsqu'un véhicule entre dans le parking
     */
    void onVehiculeEntree(String immatriculation, String type);
    
    /**
     * Méthode appelée lorsqu'un véhicule sort du parking
     */
    void onVehiculeSortie(String immatriculation, String type);
}