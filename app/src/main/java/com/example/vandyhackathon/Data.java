package com.example.vandyhackathon;

public class Data {
    String item, date, id, note;
    int amount, month;

    public Data() {
    }

    public Data(String item, String date, String id, String note, int amount, int month) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.note = note;
        this.amount = amount;
        this.month = month;
    }

    public String getItem() {
        return item;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public int getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
