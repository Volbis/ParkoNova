package org.parko.classes;

public class Statistique {
    private int id;
    private int placesOccupees;
    private double revenus;

    public Statistique(int id, int placesOccupees, double revenus) {
        this.id = id;
        this.placesOccupees = placesOccupees;
        this.revenus = revenus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlacesOccupees() {
        return placesOccupees;
    }

    public void setPlacesOccupees(int placesOccupees) {
        this.placesOccupees = placesOccupees;
    }

    public double getRevenus() {
        return revenus;
    }

    public void setRevenus(double revenus) {
        this.revenus = revenus;
    }
}