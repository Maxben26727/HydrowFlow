package com.max.hydro_flow;

public class product_data {
    private String productID, productName, productDesc, productUnit, productQty, productPrice,productExp,productpicurl;
public product_data(){}
    public product_data(String productID, String productName, String productDesc, String productUnit, String productQty, String productPrice, String productExp, String productpicurl) {
        this.productID = productID;
        this.productName = productName;
        this.productDesc = productDesc;
        this.productUnit = productUnit;
        this.productQty = productQty;
        this.productPrice = productPrice;
        this.productExp = productExp;
        this.productpicurl = productpicurl;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductpicurl() {
        return productpicurl;
    }

    public void setProductpicurl(String productpicurl) {
        this.productpicurl = productpicurl;
    }

    public String getProductExp() {
        return productExp;
    }

    public void setProductExp(String productExp) {
        this.productExp = productExp;
    }
}
