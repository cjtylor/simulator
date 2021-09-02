package com.exchange.simulator.order;


public class Order implements IOrder {

    private String orderId;
    private String instrumentId;
    private double price;
    private Side side;
    private char orderType;
    private long orderQty;
    private long openQty;
    private long cumFilledQty;
    private int timeInForce;
    private String trader;
    private long timeStampMilliSec;
    private long queuePos;

    public Order(String orderId, String instrumentId, double limitPrice, Side side, char orderType,
                 long orderQty, int timeInForce, String trader, long timeStampMilliSec) {
        this.orderId = orderId;
        this.instrumentId = instrumentId;
        this.price = limitPrice;
        this.side = side;
        this.orderType = orderType;
        this.orderQty = orderQty;
        this.openQty = orderQty;
        this.timeInForce = timeInForce;
        this.trader = trader;
        this.timeStampMilliSec = timeStampMilliSec;
    }


    @Override
    public boolean isMarketOrder() {
        return getOrderType() == OrdType.MARKET || getOrderType() == OrdType.MOC;
    }

    @Override
    public boolean isLimitOrder() {
        return getOrderType() == OrdType.LIMIT || getOrderType() == OrdType.LOC;
    }

    @Override
    public void cancel() {
        this.openQty = 0;
    }

    @Override
    public boolean isDone() {
        return orderQty == cumFilledQty;
    }

    @Override
    public boolean isClosed() {
        return openQty == 0;
    }

    @Override
    public void execute(double price, long quantity) {
        openQty -= quantity;
        cumFilledQty += quantity;
        System.out.println(side.toString() + " " + orderId + " filled " + quantity + "@" + price + " (Open=" + openQty + " / CumFill=" + cumFilledQty + " / Size=" + orderQty + " )") ;
        if(isDone()){
            System.out.println(orderId + " fully filled");
        }
    }

    public Object clone(){
        IOrder o = null;
        try{
            o = (IOrder) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", instrumentId='" + instrumentId + '\'' +
                ", limitPrice=" + price +
                ", side=" + side +
                ", orderType='" + orderType + '\'' +
                ", orderQty=" + orderQty +
                ", openQty=" + openQty +
                ", filledQty=" + cumFilledQty +
                ", timeInForce=" + timeInForce +
                ", trader='" + trader + '\'' +
                ", timeStampMilliSec=" + timeStampMilliSec +
                '}';
    }


    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getInstrumentId() {
        return instrumentId;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public char getOrderType() {
        return orderType;
    }

    @Override
    public long getOrderQty() {
        return orderQty;
    }

    @Override
    public long getOpenQty() {
        return openQty;
    }

    @Override
    public long getCumFilledQty() {
        return cumFilledQty;
    }

    @Override
    public int getTimeInForce() {
        return timeInForce;
    }

    @Override
    public String getTrader() {
        return trader;
    }

    @Override
    public long getTimeStampMilliSec() {
        return timeStampMilliSec;
    }


    public void setOrderQty(long orderQty) {
        this.orderQty = orderQty;
    }

    public void setOpenQty(long openQty) {
        this.openQty = openQty;
    }

    public void setCumFilledQty(long cumFilledQty) {
        this.cumFilledQty = cumFilledQty;
    }
    public long getQueuePos() {
        return queuePos;
    }

    public void setQueuePos(long queuePos) {
        this.queuePos = queuePos;
    }
}
