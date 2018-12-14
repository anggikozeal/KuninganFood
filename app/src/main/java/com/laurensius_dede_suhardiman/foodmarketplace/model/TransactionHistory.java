package com.laurensius_dede_suhardiman.foodmarketplace.model;

public class TransactionHistory {

    String id;
    String idTransaction;
    String total;
    String datetime;

    public TransactionHistory(
            String id,
            String idTransaction,
            String total,
            String datetime
    ){
        this.id = id;
        this.idTransaction = idTransaction;
        this.total = total;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public String getTotal() {
        return total;
    }

    public String getDatetime() {
        return datetime;
    }
}
