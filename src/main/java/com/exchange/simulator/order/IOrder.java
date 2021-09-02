package com.exchange.simulator.order;

public interface IOrder {

    String getOrderId();

    String getInstrumentId();

    double getPrice();

    Side getSide();

    char getOrderType();

    long getOrderQty();

    long getOpenQty();

    long getCumFilledQty();

    int getTimeInForce();

    String getTrader();

    long getTimeStampMilliSec();

    boolean isMarketOrder();

    boolean isLimitOrder();

    void cancel();

    void execute(double price, long quantity);

    boolean isDone();

    boolean isClosed();

    void setOrderQty(long orderQty);

    void setOpenQty(long openQty);

    void setCumFilledQty(long cumFilledQty);

    public long getQueuePos();
    public void setQueuePos(long pos);

}