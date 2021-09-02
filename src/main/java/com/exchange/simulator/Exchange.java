package com.exchange.simulator;

import com.exchange.simulator.marketdata.IMarketDataProvider;
import com.exchange.simulator.order.IOrder;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class Exchange implements IExchange {

    private final Logger logger = Logger.getLogger(Exchange.class);
    private final ConcurrentHashMap<String, IOrderBook> orderBookCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IOrder> ordersCache = new ConcurrentHashMap<>();

    private final IMarketDataProvider marketDataProvider;

    public Exchange(IMarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }


    @Override
    public void initOrderBook(String instrumentId) {
        IOrderBook orderbook = orderBookCache.get(instrumentId);
        if (orderbook == null) {
            orderbook = new OrderBook(instrumentId);
            orderBookCache.putIfAbsent(instrumentId, orderbook);
            marketDataProvider.registerOrderbook(orderbook);
        }
    }


    @Override
    public boolean newOrder(IOrder order) {
        boolean success = orderBookCache.get(order.getInstrumentId()).addOrder(order);
        if (success) {
            ordersCache.putIfAbsent(order.getOrderId(), order);
        }
        return success;
    }


    @Override
    public boolean cancelOrder(String orderId) {
        if (orderId == null || !ordersCache.containsKey(orderId)) {
            logger.warn("can not cancel " + orderId);
        }
        IOrder order = ordersCache.get(orderId);
        if (order.isMarketOrder()) {
            logger.warn("can not cancel market order");
            return false;
        }
        if (order.isClosed()) {
            logger.warn("order is closed");
            return false;
        }
        boolean success = orderBookCache.get(order.getInstrumentId()).removeOrder(order);
        if (success) {
            ordersCache.remove(orderId);
        }
        return success;
    }

    @Override
    public IOrderBook getOrderBook(String instrumentId) {
        return orderBookCache.get(instrumentId);
    }

}
