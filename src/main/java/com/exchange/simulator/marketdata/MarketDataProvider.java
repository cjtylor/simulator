package com.exchange.simulator.marketdata;

import com.exchange.simulator.IOrderBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * a simple market data provider to simulate:
 * 1. market data event.
 * 2. market trade event
 *
 * @author Tylor
 */
public class MarketDataProvider implements IMarketDataProvider {

    private final Map<String, IOrderBook> orderbookMap = new ConcurrentHashMap<>();

    @Override
    public void onMarketDataUpdate(MarketDataUpdate event) {
        IOrderBook orderBook = orderbookMap.get(event.getSymbol());
        if (orderBook == null)
            return;
        orderBook.onMarketDataUpdate(event.getBidData(), true, event.getBestBid());
        orderBook.onMarketDataUpdate(event.getAskData(), false, event.getBestAsk());
    }

    @Override
    public void onMarketTradeUpdate(MarketTradeUpdate event) {
        IOrderBook orderBook = orderbookMap.get(event.getSymbol());
        if (orderBook == null)
            return;
        orderBook.onMarketTrade(event);
    }

    @Override
    public void registerOrderbook(IOrderBook orderBook) {
        this.orderbookMap.putIfAbsent(orderBook.getInstrumentId(), orderBook);
    }
}
