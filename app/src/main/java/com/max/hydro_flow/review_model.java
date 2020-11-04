package com.max.hydro_flow;

public class review_model {
    float rating;
    String customerID;
    String feedback;
    String date;

    public review_model() {

    }

    public review_model(float rating, String customerID, String feedback, String date) {
        this.rating = rating;
        this.customerID = customerID;
        this.feedback = feedback;
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
