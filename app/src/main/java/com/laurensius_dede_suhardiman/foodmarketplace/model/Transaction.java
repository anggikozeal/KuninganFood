package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    String id;
    String idShop;
    String idUser;
    String status;
    String image;
    String datetime;
    Shop shop;
    List<TransactionDetail> transactionDetailList = new ArrayList<>();

    public Transaction(
            String id,
            String idShop,
            String idUser,
            String status,
            String image,
            String datetime,
            Shop shop,
            List<TransactionDetail> transactionDetailList
    ){
        this.id = id;
        this.idShop = idShop;
        this.idUser = idUser;
        this.status = status;
        this.image = image;
        this.datetime = datetime;
        this.shop = shop;
        this.transactionDetailList = transactionDetailList;
    }

    public String getId() {
        return id;
    }

    public String getIdShop() {
        return idShop;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getDatetime() {
        return datetime;
    }

    public Shop getShop() {
        return shop;
    }

    public List<TransactionDetail> getTransactionDetailList() {
        return transactionDetailList;
    }
}
