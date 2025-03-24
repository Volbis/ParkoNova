package org.parko.classes;

import java.util.Date;

public class Historique {
    private int id;
    private String action;
    private Date date;

    public Historique(int id, String action, Date date) {
        this.id = id;
        this.action = action;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}