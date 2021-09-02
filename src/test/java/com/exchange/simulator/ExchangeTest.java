package com.exchange.simulator;

import com.exchange.simulator.marketdata.MarketDataProvider;
import com.exchange.simulator.order.IOrder;
import com.exchange.simulator.order.OrdType;
import com.exchange.simulator.order.OrderBuilder;
import com.exchange.simulator.order.Side;
import com.exchange.simulator.util.IdGenerator;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

public class ExchangeTest {

    @Test
    public void testExchangeAddOrder() {
        Exchange exchange = new Exchange(new MarketDataProvider());
        String symbol = "ABC";
        exchange.initOrderBook(symbol);
        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();

        exchange.newOrder(o1);
        exchange.newOrder(o2);
        assertTrue(exchange.getOrderBook(symbol) != null);
        assertTrue(exchange.getOrderBook(symbol).getInstrumentId() == symbol);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 2);

        IOrder o3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(20).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2025).createOrder();
        exchange.newOrder(o3);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 2);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 0);
    }

    @Test
    public void testExchangeCancelOrder() {
        Exchange exchange = new Exchange(new MarketDataProvider());
        String symbol = "ABC";
        exchange.initOrderBook(symbol);
        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        exchange.newOrder(o1);

        IOrder o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        exchange.newOrder(o2);
        assertTrue(true == exchange.cancelOrder(o2.getOrderId()));


        IOrder o3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2025).createOrder();
        exchange.newOrder(o3);
        assertTrue(false == exchange.cancelOrder(o1.getOrderId()));
        assertTrue(false == exchange.cancelOrder(o3.getOrderId()));
    }
}
