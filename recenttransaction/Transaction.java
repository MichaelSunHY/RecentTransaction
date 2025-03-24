package com.example.recenttransaction;

public class Transaction {
    private int id;
    private String name;
    private String category;
    private long date;
    private double amount;

    public Transaction(int id, String name, String category, long date, double amount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public long getDate() { return date; }
    public double getAmount() { return amount; }
}

