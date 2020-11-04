package com.max.hydro_flow;

public class cart_data {

public cart_data(){}


    String productID;
    String productName;
    String productPrice;
    String productImageUrl;
    String cartqty;
    String totalPrice;
    String productUnit;


    public Boolean getRated_state() {
        return rated_state;
    }

    public void setRated_state(Boolean rated_state) {
        this.rated_state = rated_state;
    }

    Boolean rated_state;

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public cart_data(String productID, String productName, String productPrice, String productImageUrl, String cartqty, String totalPrice, String productUnit,Boolean rated_state) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.cartqty = cartqty;
        this.totalPrice = totalPrice;
        this.productID = productID;

        this.productUnit = productUnit;
        this.rated_state = rated_state;
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getCartqty() {
        return cartqty;
    }

    public void setCartqty(String cartqty) {
        this.cartqty = cartqty;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String grandtotal) {
        this.totalPrice = grandtotal;
    }
}
