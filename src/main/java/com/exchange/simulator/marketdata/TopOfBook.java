package com.exchange.simulator.marketdata;


public class TopOfBook {
    private double price;
    private long qty;

    public TopOfBook(double price, long qty) {
        this.price = price;
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public long getQty() {
        return qty;
    }
}
