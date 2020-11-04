package com.max.hydro_flow;

public class user_data {
    public user_data(){}
    private String customerID;
    private String name;
    private String address;
    private String pancardNo;
    private String gstinNo;
    private String contactno;
    private String picurl;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    private String areaName;
    private String balance;

    public user_data(String customerID, String name, String address, String pancardNo, String gstinNo, String contactno, String picurl, String areaName,String balance) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.pancardNo = pancardNo;
        this.gstinNo = gstinNo;
        this.contactno = contactno;
        this.picurl = picurl;
        this.areaName = areaName;
        this.balance = balance;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getPancardNo() {
        return pancardNo;
    }

    public void setPancardNo(String pancardNo) {
        this.pancardNo = pancardNo;
    }

    public String getGstinNo() {
        return gstinNo;
    }

    public void setGstinNo(String gstinNo) {
        this.gstinNo = gstinNo;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
