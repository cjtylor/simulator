package com.exchange.simulator;

import com.exchange.simulator.marketdata.MarketTradeUpdate;
import com.exchange.simulator.marketdata.TopOfBook;
import com.exchange.simulator.order.IOrder;
import com.exchange.simulator.order.Side;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Interface for order book
 *
 * @author Tylor
 */
public interface IOrderBook {
    /**
     * Add a new order to order book
     *
     * @param order
     * @return true if success
     */
    boolean addOrder(IOrder order);

    /**
     * remove and cancel order from order book
     *
     * @param order
     * @return true if success
     */
    boolean removeOrder(IOrder order);

    /**
     * @return Instrument Id of the order book
     */
    String getInstrumentId();

    /**
     * @return Bid order queue
     */
    Queue<IOrder> getBidOrders();

    /**
     * @return Ask order queue
     */
    Queue<IOrder> getAskOrders();


    /**
     * handles the market data updates and generates dummy orders to simulate orders from other participates on the exchange.
     * @param incomingMarketEvent
     * @param isBid
     * @param bestPx
     */
    void onMarketDataUpdate(Map<Double, Long> incomingMarketEvent, boolean isBid, double bestPx);

    /**
     * handles the market trade updates and re-estimate order position in the queue
     *
     */
    void onMarketTrade(MarketTradeUpdate event);

    /**
     * @param side
     * @return best price on market
     */
    double getBestPrice(Side side);

    /**
     * @param side
     * @return top of book on market
     */
    TopOfBook getTopOfBook(Side side);

    /**
     * @param side
     * @param px
     * @return level 2 support
     */
    Set<IOrder> getOrdersAtPrice(Side side, double px);

    /**
     * @param side
     * @param px
     * @return liquidity on market
     */
    long getLiquidityAtPrice(Side side, double px);

}
