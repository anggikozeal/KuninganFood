package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.io.Serializable;

public class Product implements Serializable {

    String id;
    String idShop;
    String name;
    String category;
    String status;
    String price;
    String discount;
    String description;
    String rating;
    String image;
    Shop shop;

    public Product(
            String id,
            String idShop,
            String name,
            String category,
            String status,
            String price,
            String discount,
            String description,
            String rating,
            String image,
            Shop shop
    ){
        this.id = id;
        this.idShop = idShop;
        this.name = name;
        this.category = category;
        this.status = status;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.rating = rating;
        this.image = image;
        this.shop = shop;
    }

    public String getId() {
        return id;
    }

    public String getIdShop() {
        return idShop;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public Shop getShop() {
        return shop;
    }
}
