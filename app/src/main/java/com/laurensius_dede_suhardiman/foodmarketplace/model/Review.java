package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.io.Serializable;

public class Review implements Serializable {

    String id;
    String idProduct;
    String idUser;
    String rating;
    String review;
    Product product;
    User user;

    public Review(
            String id,
            String idProduct,
            String idUser,
            String rating,
            String review,
            Product product,
            User user

    ){
        this.id = id;
        this.idProduct = idProduct;
        this.idUser = idUser;
        this.rating = rating;
        this.review = review;
        this.product = product;
        this.user = user;
    }


    public String getId() {
        return id;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }
}
