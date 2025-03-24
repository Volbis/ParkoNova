package org.parko.classes;

import java.util.Date;

public class Reservation {
    private int id;
    private int placeId;
    private int vehiculeId;
    private Date dateReservation;

    public Reservation(int id, int placeId, int vehiculeId, Date dateReservation) {
        this.id = id;
        this.placeId = placeId;
        this.vehiculeId = vehiculeId;
        this.dateReservation = dateReservation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public int getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(int vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }
}