package com.laurensius_dede_suhardiman.foodmarketplace.model;

public class Notifikasi {

    String id;
    String idUser;
    String title;
    String message;
    String datetime;

    public Notifikasi(
        String id,
        String idUser,
        String title,
        String message,
        String datetime
    ){
        this.id = id;
        this.idUser = idUser;
        this.title = title;
        this.message = message;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDatetime() {
        return datetime;
    }
}
