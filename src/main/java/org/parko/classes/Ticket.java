package org.parko.classes;

import java.util.Date;

public class Ticket {
    private int id;
    private int vehiculeId;
    private Date dateEmission;
    private double cout;

    public Ticket(int id, int vehiculeId, Date dateEmission, double cout) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.dateEmission = dateEmission;
        this.cout = cout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(int vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public Date getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(Date dateEmission) {
        this.dateEmission = dateEmission;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }
}