package org.parko.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire d'événements singleton pour gérer les entrées et sorties de véhicules dans le parking.
 */
public class ParkingEventManager {
    
    private static ParkingEventManager instance;
    private List<ParkingEventListener> listeners = new ArrayList<>();
    
    private ParkingEventManager() {
        // Constructeur privé pour le singleton
    }
    
    /**
     * Obtient l'instance unique du gestionnaire d'événements.
     */
    public static synchronized ParkingEventManager getInstance() {
        if (instance == null) {
            instance = new ParkingEventManager();
        }
        return instance;
    }
    
    /**
     * Ajoute un écouteur d'événements.
     * @param listener L'écouteur à ajouter
     */
    public void addListener(ParkingEventListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Retire un écouteur d'événements.
     * @param listener L'écouteur à retirer
     */
    public void removeListener(ParkingEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Déclenche l'événement d'entrée d'un véhicule.
     * @param immatriculation L'immatriculation du véhicule
     * @param type Le type du véhicule (VOITURE ou MOTO)
     */
    public void fireVehiculeEntree(String immatriculation, String type) {
        System.out.println("Notification d'entrée: " + immatriculation + " (" + type + ")");
        for (ParkingEventListener listener : listeners) {
            listener.onVehiculeEntree(immatriculation, type);
        }
    }
    
    /**
     * Déclenche l'événement de sortie d'un véhicule.
     * @param immatriculation L'immatriculation du véhicule
     * @param type Le type du véhicule (VOITURE ou MOTO)
     */
    public void fireVehiculeSortie(String immatriculation, String type) {
        System.out.println("Notification de sortie: " + immatriculation + " (" + type + ")");
        for (ParkingEventListener listener : listeners) {
            listener.onVehiculeSortie(immatriculation, type);
        }
    }
}