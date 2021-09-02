package com.exchange.simulator.order;

import java.util.Comparator;


/**
 * Default order comparator based on price / time  priority.
 * If price and time are same, compare order id as last resort.
 *
 * @author Tylor
 */
public class OrderComaparator implements Comparator<IOrder> {

    @Override
    public int compare(IOrder o1, IOrder o2) {
        if (o1.isMarketOrder() && !o2.isMarketOrder())
            return -1;
        if (!o1.isMarketOrder() && o2.isMarketOrder())
            return 1;
        if (o1.isMarketOrder() && o2.isMarketOrder())
            return compareOrderTime(o1, o2);

        //compare buy order
        if (o1.getSide() == Side.BUY && o2.getSide() == Side.BUY) {
            if (o1.getPrice() > o2.getPrice()) {
                return -1;
            }
            if (o1.getPrice() < o2.getPrice()) {
                return 1;
            }
            return compareOrderTime(o1, o2);
        }
        //compare sell order
        if (o1.getSide() == Side.SELL && o2.getSide() == Side.SELL) {
            if (o1.getPrice() < o2.getPrice()) {
                return -1;
            }
            if (o1.getPrice() > o2.getPrice()) {
                return 1;
            }
            return compareOrderTime(o1, o2);
        }
        return compareOrderTime(o1, o2);
    }

    private int compareOrderTime(IOrder o1, IOrder o2) {
        if (o1.getTimeStampMilliSec() < o2.getTimeStampMilliSec())
            return -1;
        if (o1.getTimeStampMilliSec() > o2.getTimeStampMilliSec())
            return 1;
        //last resort, compare order Id
        return o1.getOrderId().compareTo(o2.getOrderId());
    }
}
