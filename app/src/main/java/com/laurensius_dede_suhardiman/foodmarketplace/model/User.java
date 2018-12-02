package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.io.Serializable;

public class User implements Serializable {
    String id;
    String username;
    String password;
    String fullName;
    String address;
    String phone;
    String lastLogin;

    public User(
            String id,
            String username,
            String password,
            String fullName,
            String address,
            String phone,
            String lastLogin
    ){
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.lastLogin = lastLogin;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getLastLogin() {
        return lastLogin;
    }
}
