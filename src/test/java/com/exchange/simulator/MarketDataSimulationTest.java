package com.exchange.simulator;

import com.exchange.simulator.marketdata.IMarketDataProvider;
import com.exchange.simulator.marketdata.MarketDataProvider;
import com.exchange.simulator.marketdata.MarketDataUpdate;
import com.exchange.simulator.marketdata.MarketTradeUpdate;
import com.exchange.simulator.order.IOrder;
import com.exchange.simulator.order.OrdType;
import com.exchange.simulator.order.OrderBuilder;
import com.exchange.simulator.order.Side;
import com.exchange.simulator.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class MarketDataSimulationTest {

    private String symbol = "Sym1";
    private IExchange exchange;
    private IMarketDataProvider marketDataProvider;

    @Before
    public void setup() {
        marketDataProvider = new MarketDataProvider();
        exchange = new Exchange(marketDataProvider);
        exchange.initOrderBook(symbol);
    }

    /**
     * test case:
     * 1.receive market data
     * 2.send buy pass buy order at near touch (qty =12)
     * 3.verify order book
     */
    @Test
    public void testPassiveOrderAtNearTouchAndRestOnOrderBook() {
        MarketDataUpdate event = generateMarketEvent1(symbol);
        marketDataProvider.onMarketDataUpdate(event);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 3);

        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(99).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();

        exchange.newOrder(o1);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 4);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.BUY).getQty() == 512);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.BUY).getPrice() == 99);
    }


    /**
     * 1. receive market data
     * 2. send aggressive buy (qty = 12) and fully matches ask1.
     * 3. send aggressive buy 2 (qty = 1000), matches ask1, ask2
     * 4. verify order book
     */
    @Test
    public void testAggressiveOrderMatchedWithMarketData() {
        MarketDataUpdate event = generateMarketEvent1(symbol);
        marketDataProvider.onMarketDataUpdate(event);

        //First buy order match with ask1
        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(100).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
        exchange.newOrder(o1);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().peek().getOpenQty() == 988);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.SELL).getPrice() == 100);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.SELL).getQty() == 988);
        assertTrue(o1.isClosed());

        //send buy order lift ask1 and ask2
        IOrder o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(101).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(1000).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
        exchange.newOrder(o2);

        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 2);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().peek().getOpenQty() == 988);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.SELL).getPrice() == 101);
        assertTrue(exchange.getOrderBook(symbol).getTopOfBook(Side.SELL).getQty() == 988);
        assertTrue(o2.isClosed() == true);
    }


    /**
     * 1. receive market data
     * 2. send passive buy at near touch
     * 3. market data moves towards near touch
     * 4. verify buy order is fully filled
     */
    @Test
    public void testMarketDataMoveAndFillsPassiveOrder() {
        MarketDataUpdate event = generateMarketEvent1(symbol);
        marketDataProvider.onMarketDataUpdate(event);

        //send passive buy at near touch
        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(99).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
        exchange.newOrder(o1);

        MarketDataUpdate eventMove = generateMarketEvent2(symbol);
        marketDataProvider.onMarketDataUpdate(eventMove);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 3);
        assertTrue(o1.getCumFilledQty() == 12);
        assertTrue(o1.isClosed());
    }


    /**
     * test case:
     * 1.receive market data
     * 2.send buy pass buy order at near touch (qty =12)
     * 3.verify order position in queue
     */
    @Test
    public void testQueuePositionOfPassiveOrder() {
        MarketDataUpdate event = generateMarketEvent1(symbol);
        marketDataProvider.onMarketDataUpdate(event);
        assertTrue(exchange.getOrderBook(symbol).getBidOrders().size() == 3);
        assertTrue(exchange.getOrderBook(symbol).getAskOrders().size() == 3);

        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(99).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
        exchange.newOrder(o1);
        assertTrue(o1.getQueuePos() == 500);


        IOrder o2 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(97).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();

        exchange.newOrder(o2);
        assertTrue(o2.getQueuePos() == 500 + 12 + 500 + 500);

    }


    /**
     * test case:
     * 1.receive market data
     * 2.send buy pass buy order at near touch (qty =12)
     * 3.verify order position in queue
     */
    @Test
    public void testQueuePositionOfPassiveOrderAfterMarketTradeIsReceived() {
        MarketDataUpdate event = generateMarketEvent1(symbol);
        marketDataProvider.onMarketDataUpdate(event);

        IOrder o1 = new OrderBuilder().setOrderId(IdGenerator.genOrderID()).setInstrumentId(symbol)
                .setLimitPrice(99).setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT).setOrderQty(12).setTimeInForce(0)
                .setTrader("").setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
        exchange.newOrder(o1);
        assertTrue(o1.getQueuePos() == 500);

        MarketTradeUpdate tradeUpdate = generateMarketTradeEvent(symbol);
        marketDataProvider.onMarketTradeUpdate(tradeUpdate);
        assertTrue(o1.getQueuePos() == 498);
    }


    private MarketDataUpdate generateMarketEvent1(String instrument) {
        MarketDataUpdate event = new MarketDataUpdate();
        event.setSymbol(instrument);

        event.setAskData(new HashMap<Double, Long>() {{
            put(100d, 1000L);
            put(101d, 1000L);
            put(102d, 1000L);
        }});

        event.setBidData(new HashMap<Double, Long>() {{
            put(99d, 500L);
            put(98d, 500L);
            put(97d, 500L);
        }});
        event.setBestAsk(100d);
        event.setBestBid(99d);
        return event;
    }

    private MarketDataUpdate generateMarketEvent2(String instrument) {
        MarketDataUpdate event = new MarketDataUpdate();
        event.setSymbol(instrument);
        event.setAskData(new HashMap<Double, Long>() {{
            put(99d, 1000L);
            put(100d, 1000L);
            put(101d, 1000L);
        }});

        event.setBidData(new HashMap<Double, Long>() {{
            put(98d, 500L);
            put(97d, 500L);
            put(96d, 500L);
        }});
        event.setBestAsk(99d);
        event.setBestBid(98d);
        return event;
    }


    private MarketTradeUpdate generateMarketTradeEvent(String instrument) {
        MarketTradeUpdate event = new MarketTradeUpdate();
        event.setSymbol(instrument);
        event.setLastPx(99);
        event.setLastQty(2);
        return event;
    }
}
