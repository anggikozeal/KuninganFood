package com.laurensius_dede_suhardiman.foodmarketplace.model;

import java.io.Serializable;

public class TransactionDetail implements Serializable {

    String id;
    String idTransaction;
    String qty;
    String note;
    Product product;

    public TransactionDetail(
        String id,
        String idTransaction,
        String qty,
        String note,
        Product product
    ){
        this.id = id;
        this.idTransaction = idTransaction;
        this.qty = qty;
        this.note = note;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public String getQty() {
        return qty;
    }

    public String getNote() {
        return note;
    }

    public Product getProduct() {
        return product;
    }
}
