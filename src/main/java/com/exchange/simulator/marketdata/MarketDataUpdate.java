package com.exchange.simulator.marketdata;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple class to represent market data update.
 * assume bidData / askData is  Price / Qty pairs, for level 1 - 5.
 * this class shall be transformed from some real market data updates.
 *
 * @author Tylor
 */

public class MarketDataUpdate {
    private String symbol;
    //level 1 - 5, bid Price - Bid Qty
    private HashMap<Double, Long> bidData;
    //level 1 - 5, ask Price - ask Qty
    private HashMap<Double, Long> askData;
    private double bestBid;
    private double bestAsk;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Map<Double, Long> getBidData() {
        return bidData;
    }

    public void setBidData(HashMap<Double, Long> bidData) {
        this.bidData = bidData;
    }

    public Map<Double, Long> getAskData() {
        return askData;
    }

    public void setAskData(HashMap<Double, Long> askData) {
        this.askData = askData;
    }

    public double getBestBid() {
        return bestBid;
    }

    public void setBestBid(double bestBid) {
        this.bestBid = bestBid;
    }

    public double getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(double bestAsk) {
        this.bestAsk = bestAsk;
    }
}
