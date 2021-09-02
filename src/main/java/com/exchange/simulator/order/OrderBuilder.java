package com.exchange.simulator.order;

public class OrderBuilder {
    private String orderId;
    private String instrumentId;
    private double limitPrice;
    private Side side;
    private char orderType;
    private long orderQty;
    private int timeInForce;
    private String trader;
    private long timeStampMilliSec;

    public OrderBuilder setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderBuilder setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
        return this;
    }

    public OrderBuilder setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
        return this;
    }

    public OrderBuilder setSide(Side side) {
        this.side = side;
        return this;
    }

    public OrderBuilder setOrderType(char orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder setOrderQty(long orderQty) {
        this.orderQty = orderQty;
        return this;
    }

    public OrderBuilder setTimeInForce(int timeInForce) {
        this.timeInForce = timeInForce;
        return this;
    }

    public OrderBuilder setTrader(String trader) {
        this.trader = trader;
        return this;
    }

    public OrderBuilder setTimeStampMilliSec(long timeStampMilliSec) {
        this.timeStampMilliSec = timeStampMilliSec;
        return this;
    }

    public IOrder createOrder() {
        return new Order(orderId, instrumentId, limitPrice, side, orderType, orderQty, timeInForce, trader, timeStampMilliSec);
    }
}