package com.exchange.simulator.marketdata;

/**
 * A simple class to represent trade events from markets.
 * it is used to estimate when passive orders will be filled.
 */
public class MarketTradeUpdate {
    private double lastPx;
    private long lastQty;
    private String symbol;


    public double getLastPx() {
        return lastPx;
    }

    public void setLastPx(double lastPx) {
        this.lastPx = lastPx;
    }

    public long getLastQty() {
        return lastQty;
    }

    public void setLastQty(long lastQty) {
        this.lastQty = lastQty;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


}
