package com.exchange.simulator;


import com.exchange.simulator.order.IOrder;


/**
 * Interface of the exchange
 *
 * @author Tylor
 */
public interface IExchange {

    /**
     * place a new order to orderbook
     *
     * @param order
     * @return true if it is successful
     */
    boolean newOrder(IOrder order);


    /**
     * Cancel an order from order book
     *
     * @param orderId
     * @return true if cancel is successful.
     */
    boolean cancelOrder(String orderId);


    /**
     * @param instrumentId
     * @return orderBookCache
     */
    IOrderBook getOrderBook(String instrumentId);

    /**
     * init a new orderbook
     * @param instrumentId
     */
    void initOrderBook(String instrumentId);
}
