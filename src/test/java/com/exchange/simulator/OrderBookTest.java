package com.exchange.simulator;

import com.exchange.simulator.order.*;
import com.exchange.simulator.util.IdGenerator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderBookTest {
    private IOrderBook orderBook;
    private String instrumentId = "HSBC bank";
    private Mockery context;

    @Before
    public void setup(){
        orderBook = new OrderBook(instrumentId);
        context = new Mockery();
    }

    @Test
    public void testMatchLimitOrderWithMarketOrder(){

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);

        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(sell1);

        assertTrue(buy1.getCumFilledQty() == 10);
        assertTrue(buy2.getCumFilledQty() == 5);
        assertTrue(buy3.getCumFilledQty() == 0);
        assertTrue(sell1.getCumFilledQty() == 15);
        assertTrue(orderBook.getBidOrders().size() == 2);
        assertTrue(orderBook.getAskOrders().size() == 0);

        IOrder sell2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(30).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(sell2);
        assertTrue(buy1.getCumFilledQty() == 10);
        assertTrue(buy2.getCumFilledQty() == 10);
        assertTrue(buy3.getCumFilledQty() == 10);
        assertTrue(sell1.getCumFilledQty() == 15);
        assertTrue(sell2.getCumFilledQty() == 15);
        assertTrue(sell2.getOpenQty() == 15);
        assertTrue(orderBook.getBidOrders().size() == 0);
        assertTrue(orderBook.getAskOrders().size() == 1);

    }

    @Test
    public void testMatchLimitOrderWithLimitOrder(){

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);

        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(11).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(sell1);

        assertTrue(buy1.getCumFilledQty() == 10);
        assertTrue(buy2.getCumFilledQty() == 0);
        assertTrue(buy3.getCumFilledQty() == 0);
        assertTrue(sell1.getCumFilledQty() == 10);
        assertTrue(orderBook.getBidOrders().size() == 2);
        assertTrue(orderBook.getAskOrders().size() == 1);
    }


    @Test
    public void testLimitOrderCannotMatch(){

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(11).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder buy3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);

        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(11.05).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(sell1);

        assertTrue(buy1.getCumFilledQty() == 0);
        assertTrue(buy2.getCumFilledQty() == 0);
        assertTrue(buy3.getCumFilledQty() == 0);
        assertTrue(sell1.getCumFilledQty() == 0);
        assertTrue(orderBook.getBidOrders().size() == 3);
        assertTrue(orderBook.getAskOrders().size() == 1);
    }


    @Test
    public void testMatchMarketOrderWithMarketOrder(){

        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder sell2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2025).createOrder();
        IOrder sell3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.SELL).setOrderType(OrdType.MARKET).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2020).createOrder();
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.BUY).setOrderType(OrdType.MARKET).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(buy1);

        assertTrue(sell1.getCumFilledQty() == 5);
        assertTrue(sell2.getCumFilledQty() == 0);
        assertTrue(sell3.getCumFilledQty() == 10);
        assertTrue(buy1.getCumFilledQty() == 15);
        assertTrue(orderBook.getBidOrders().size() == 0);
        assertTrue(orderBook.getAskOrders().size() == 2);

    }

    @Test
    public void testGetTopOfBook() {
        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(50).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(20).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder sell2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(50).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2025).createOrder();
        IOrder sell3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(51).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(8).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2020).createOrder();
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(49).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(25).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        IOrder buy2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(48).setSide(Side.BUY).setOrderType(OrdType.LIMIT).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);

        assertTrue(orderBook.getTopOfBook(Side.SELL).getPrice() == 50);
        assertTrue(orderBook.getTopOfBook(Side.SELL).getQty() == 30);
        assertTrue(orderBook.getTopOfBook(Side.BUY).getPrice() == 49);
        assertTrue(orderBook.getTopOfBook(Side.BUY).getQty() == 25);

        assertTrue(orderBook.getLiquidityAtPrice(Side.SELL, 51) == 8 );
    }


    @Test
    public void testRemoveOrder(){

        IOrder sell1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2022).createOrder();
        IOrder sell2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2021).createOrder();
        IOrder sell3 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(10).setSide(Side.SELL).setOrderType(OrdType.LIMIT).setOrderQty(10).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2020).createOrder();
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);

        IOrder buy1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(instrumentId).setLimitPrice(0).setSide(Side.BUY).setOrderType(OrdType.MARKET).setOrderQty(15).setTimeInForce(0).setTrader("").setTimeStampMilliSec(2026).createOrder();
        orderBook.addOrder(buy1);

        assertTrue(sell3.isClosed());
        assertTrue(orderBook.removeOrder(sell3) == false);


        assertTrue(orderBook.removeOrder(sell2) == true);
        assertTrue(sell2.isClosed());
        assertTrue(orderBook.removeOrder(sell1) == true);
        assertTrue(sell1.isClosed());

    }

    @Test
    public void testInsertBidOrder(){
        final IOrder buy1 = context.mock(IOrder.class, "buy1");
        buildMockExpectation(buy1, Side.BUY, 60, 2001, false, "1");
        final IOrder buy2 = context.mock(IOrder.class, "buy2");
        buildMockExpectation(buy2, Side.BUY, 60, 2000, false, "2");
        final IOrder buy3 = context.mock(IOrder.class, "buy3");
        buildMockExpectation(buy3, Side.BUY, 60.05, 2003, false, "3");
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        assertTrue(orderBook.getBidOrders().size() == 3);
        assertSame(buy3, orderBook.getBidOrders().poll());
        assertSame(buy2, orderBook.getBidOrders().poll());
        assertSame(buy1, orderBook.getBidOrders().poll());
    }

    @Test
    public void testInsertAskOrder(){
        final IOrder sell1 = context.mock(IOrder.class, "sell1");
        buildMockExpectation(sell1, Side.SELL, 60, 2001, false, "1");
        final IOrder sell2 = context.mock(IOrder.class, "sell2");
        buildMockExpectation(sell2, Side.SELL, 60, 2000, false, "2");
        final IOrder sell3 = context.mock(IOrder.class, "sell3");
        buildMockExpectation(sell3, Side.SELL, 60.05, 2003, false, "3");

        final IOrder sell4 = context.mock(IOrder.class, "sell4");
        buildMockExpectation(sell4, Side.SELL, 60.00d, 2005, true, "4");

        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        assertTrue(orderBook.getAskOrders().size() == 4);
        assertSame(sell4, orderBook.getAskOrders().poll());
        assertSame(sell2, orderBook.getAskOrders().poll());
        assertSame(sell1, orderBook.getAskOrders().poll());
        assertSame(sell3, orderBook.getAskOrders().poll());
    }

    private void buildMockExpectation(IOrder order, Side side, double px, long timestampMilliSec, boolean isMarketOrder, String orderId){
        context.checking(new Expectations(){{
            allowing(order).getSide();
            will(returnValue(side));
            allowing(order).getPrice();
            will(returnValue(px));
            allowing(order).getOrderId();
            will(returnValue(orderId));
            allowing(order).getTimeStampMilliSec();
            will(returnValue(timestampMilliSec));
            allowing(order).isMarketOrder();
            will(returnValue(isMarketOrder));
            allowing(order).getTrader();
            will(returnValue("testTrader"));
            allowing(order).setQueuePos(with(any(Long.class)));
            allowing(order).getOpenQty();
            will(returnValue(0L));
        }});
    }

}
