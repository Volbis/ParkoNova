package org.parko.classes;

public class Place {
    private int id;
    private int numero;
    private boolean estOccupee;
    private int parkingId;

    public Place(int id, int numero, boolean estOccupee, int parkingId) {
        this.id = id;
        this.numero = numero;
        this.estOccupee = estOccupee;
        this.parkingId = parkingId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isEstOccupee() {
        return estOccupee;
    }

    public void setEstOccupee(boolean estOccupee) {
        this.estOccupee = estOccupee;
    }

    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }
}