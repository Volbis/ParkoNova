package org.parko.classes;

public class Vehicule {
    private int id;
    private String plaqueImmatriculation;
    private String type;

    public Vehicule(int id, String plaqueImmatriculation, String type) {
        this.id = id;
        this.plaqueImmatriculation = plaqueImmatriculation;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaqueImmatriculation() {
        return plaqueImmatriculation;
    }

    public void setPlaqueImmatriculation(String plaqueImmatriculation) {
        this.plaqueImmatriculation = plaqueImmatriculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}