package com.example.miniprojet;

import java.util.Date;

public class Formation {

    public String description;
    public String title;
    public String dateDebut;
    public String dateFin;
    public String img;

    public Formation(){

    }
    public Formation( String description, String title, String dateDebut, String dateFin, String img) {
        this.description = description;
        this.title = title;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
