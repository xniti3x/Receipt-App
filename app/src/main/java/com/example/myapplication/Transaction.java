package com.example.myapplication;

public class Transaction {

    private String transactionId;
    private String title;
    private String remittanceInformationStructured;
    private String transactionAmount;
    private String bookingDate;

    public Transaction(String transactionId, String title, String remittanceInformationStructured, String transactionAmount, String bookingDate) {
        this.transactionId = transactionId;
        this.title = title;
        this.remittanceInformationStructured = remittanceInformationStructured;
        this.transactionAmount = transactionAmount;
        this.bookingDate=bookingDate;
    }

    public String getBookingDate() {

        bookingDate=bookingDate.trim().replaceAll(" +", " ");
        if(bookingDate.length()>10){
            bookingDate=bookingDate.substring(0,10);
        }
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemittanceInformationStructured() {
        remittanceInformationStructured=remittanceInformationStructured.trim().replaceAll(" +", " ");
        if(remittanceInformationStructured.length()>42){
            remittanceInformationStructured = remittanceInformationStructured.substring(0,32);
        }
        return remittanceInformationStructured;
    }

    public void setRemittanceInformationStructured(String remittanceInformationStructured) {
        this.remittanceInformationStructured = remittanceInformationStructured;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString(){
        int max_length=35;
        String s = transactionAmount +" - "+ title;
        s=s.trim().replaceAll(" +", " ");
        if(s.length()>max_length){
            s= s.substring(0,max_length)+"..";
        }
        return s;
    }
}
