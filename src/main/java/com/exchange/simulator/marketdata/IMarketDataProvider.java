package com.exchange.simulator.marketdata;

import com.exchange.simulator.IOrderBook;

/**
 * @author Tylor
 * market data simulator
 */
public interface IMarketDataProvider {

    void onMarketDataUpdate(MarketDataUpdate event);
    void onMarketTradeUpdate(MarketTradeUpdate event);
    void registerOrderbook(IOrderBook orderBook);



}
