package org.parko.classes;

import java.time.LocalDate;

public class Statistique {
    private LocalDate date;
    private int nbVehicules;
    private double revenusTotal;
    private int frequentation;

    // Getters et Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getNbVehicules() {
        return nbVehicules;
    }

    public void setNbVehicules(int nbVehicules) {
        this.nbVehicules = nbVehicules;
    }

    public double getRevenusTotal() {
        return revenusTotal;
    }

    public void setRevenusTotal(double revenusTotal) {
        this.revenusTotal = revenusTotal;
    }

    public int getFrequentation() {
        return frequentation;
    }

    public void setFrequentation(int frequentation) {
        this.frequentation = frequentation;
    }
}