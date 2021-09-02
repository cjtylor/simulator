package com.exchange.simulator;

import com.exchange.simulator.order.*;
import com.exchange.simulator.util.IdGenerator;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;


public class OrderComparatorTest {
    Comparator<IOrder> orderComparator = new OrderComaparator();

    @Test
    public void testSellOrderCompare() {
        String symbol = "ANZ";
        IOrder o1, o2;

        o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(1, orderComparator.compare(o1, o2));

        o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();
        assertEquals(-1, orderComparator.compare(o1, o2));

        o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(-1, orderComparator.compare(o1, o2));


        o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(1, orderComparator.compare(o1, o2));

        o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(9).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(-1, orderComparator.compare(o1, o2));
    }

    @Test
    public void testBuyOrderCompare() {
        String symbol = "AIA.AX";
        IOrder order1, order2;

        order1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        order2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.MARKET).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(1, orderComparator.compare(order1, order2));

        order1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.MARKET).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        order2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.MARKET).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(-1, orderComparator.compare(order1, order2));

        order1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();
        order2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(1, orderComparator.compare(order1, order2));


        order1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        order2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(-1, orderComparator.compare(order1, order2));

        order1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(9).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(100).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        order2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(101).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        assertEquals(1, orderComparator.compare(order1, order2));
    }


}

