package org.parko.classes;

public class Tarification {
    private int id;
    private double tauxHoraire;
    private double reduction;

    public Tarification(int id, double tauxHoraire, double reduction) {
        this.id = id;
        this.tauxHoraire = tauxHoraire;
        this.reduction = reduction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTauxHoraire() {
        return tauxHoraire;
    }

    public void setTauxHoraire(double tauxHoraire) {
        this.tauxHoraire = tauxHoraire;
    }

    public double getReduction() {
        return reduction;
    }

    public void setReduction(double reduction) {
        this.reduction = reduction;
    }
}