package com.laurensius_dede_suhardiman.foodmarketplace.model;

public class Drawdown {

    String id;
    String idShop;
    String total;
    String status;
    String datetimeRequest;
    String datetimeApprove;

    public Drawdown(
            String id,
            String idShop,
            String total,
            String status,
            String datetimeRequest,
            String datetimeApprove){

        this.id = id ;
        this.idShop = idShop ;
        this.total = total ;
        this.status = status ;
        this.datetimeRequest = datetimeRequest ;
        this.datetimeApprove = datetimeApprove ;
    }

    public String getId() {
        return id;
    }

    public String getIdShop() {
        return idShop;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getDatetimeRequest() {
        return datetimeRequest;
    }

    public String getDatetimeApprove() {
        return datetimeApprove;
    }
}
