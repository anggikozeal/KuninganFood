package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.io.Serializable;

public class Shop implements Serializable {
    String id;
    String idUser;
    String shopName;
    String address;
    User user;

    public Shop(String id, String idUser, String shopName, String address, User user){
        this.id = id;
        this.idUser = idUser;
        this.shopName = shopName;
        this.address = address;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAddress() {
        return address;
    }

    public User getUser() {
        return user;
    }
}
