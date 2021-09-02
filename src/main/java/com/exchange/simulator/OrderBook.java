package com.exchange.simulator;

import com.exchange.simulator.marketdata.MarketTradeUpdate;
import com.exchange.simulator.marketdata.TopOfBook;
import com.exchange.simulator.order.*;
import com.exchange.simulator.util.IdGenerator;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple implementation of IOrderbook.
 *
 * @author Tylor
 */
public class OrderBook implements IOrderBook {

    private final static Comparator<IOrder> orderComparator = new OrderComaparator();
    private final Logger logger = Logger.getLogger(OrderBook.class);
    private final String instrumentId;
    private final Queue<IOrder> bidOrders = new PriorityQueue<>(10, orderComparator);
    private final Queue<IOrder> askOrders = new PriorityQueue<>(10, orderComparator);
    private final Map<Double, HashSet<IOrder>> bidMarketByPrice = new ConcurrentHashMap<>();
    private final Map<Double, HashSet<IOrder>> askMarketByPrice = new ConcurrentHashMap<>();
    private final String exchSim = "ExchSim";

    private Map<String, Long> queuePosition = new ConcurrentHashMap<>();

    public OrderBook(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    @Override
    public String getInstrumentId() {
        return instrumentId;
    }

    @Override
    public Queue<IOrder> getBidOrders() {
        return bidOrders;
    }

    @Override
    public Queue<IOrder> getAskOrders() {
        return askOrders;
    }

    @Override
    public void onMarketDataUpdate(Map<Double, Long> incoming, boolean isBid, double bestPx) {
        Queue<IOrder> orders = isBid ? bidOrders : askOrders;
        Iterator<IOrder> it = orders.iterator();
        Map<Double, Integer> hasNonSimOrder = new HashMap<>();
        Map<Double, Long> existing = new HashMap<>();
        while (it.hasNext()) {
            IOrder o = it.next();
            if (!o.getTrader().equals(exchSim)) {
                hasNonSimOrder.put(o.getPrice(), 1);
            } else {
                //adjustOrPut
                existing.putIfAbsent(o.getPrice(), Long.valueOf(0));
                existing.put(o.getPrice(), existing.get(o.getPrice()) + o.getOpenQty());
            }
        }

        Set<Double> removeSet = new HashSet<>();
        for (Double key : existing.keySet()) {
            if (!incoming.containsKey(key)) {
                removeSet.add(key);
            }
        }

        Map<Double, Long> addMap = new HashMap<>();
        Map<Double, Long> amendMap = new HashMap<>();
        for (Double key : incoming.keySet()) {
            long value = incoming.get(key);
            if (!existing.containsKey(key)) {
                addMap.put(key, value);
            } else {
                long amendValue = value - existing.get(key);
                if (amendValue != 0) {
                    amendMap.put(key, amendValue);
                }
            }
        }

        //traverse orders and update
        Object[] orderArray = orders.toArray();
        for (int i = orderArray.length - 1; i >= 0; i--) {
            IOrder o = (Order) orderArray[i];
            if (o.getTrader().equals(exchSim)) {
                if (removeSet.contains(o.getPrice())) {
                    orders.remove(o);
                } else if (amendMap.containsKey(o.getPrice())) {
                    long amendValue = amendMap.get(o.getPrice());
                    if (amendValue < 0) {
                        long newOpenQty = o.getOpenQty() + amendValue;
                        if (newOpenQty <= 0) {
                            if (newOpenQty == 0) {
                                amendMap.remove(o.getPrice());
                            } else {
                                amendMap.put(o.getPrice(), amendMap.get(o.getPrice()) + o.getOpenQty());
                            }
                            o.cancel();
                            orders.remove(o);
                        } else {
                            o.setOrderQty(o.getCumFilledQty() + newOpenQty);
                            o.setOpenQty(newOpenQty);
                            amendMap.remove(o.getPrice());
                        }
                    } else if (amendValue > 0) {
                        if (hasNonSimOrder.get(o.getPrice()) == 0) {
                            long newOpenQty = o.getOpenQty() + amendValue;
                            o.setOpenQty(o.getCumFilledQty() + newOpenQty);
                            o.setOpenQty(newOpenQty);
                            amendMap.remove(o.getPrice());
                        }
                    }
                }
            } else {
                //now can execute
                if ((isBid && o.getPrice() > bestPx) || (!isBid && o.getPrice() < bestPx)) {
                    o.execute(o.getPrice(), o.getOpenQty());
                    orders.remove(o);
                    System.out.println(exchSim + " filled " + o.getOrderId());
                }
            }
        }

        for (Double key : addMap.keySet()) {
            double price = key;
            if (price > 0) {
                long qty = addMap.get(price);
                if (qty > 0) {
                    IOrder newOrder = new OrderBuilder().setOrderId(IdGenerator.genOrderID())
                            .setInstrumentId(this.instrumentId)
                            .setLimitPrice(price)
                            .setSide(isBid ? Side.BUY : Side.SELL)
                            .setOrderType(OrdType.LIMIT)
                            .setOrderQty(qty)
                            .setTimeInForce(0).setTrader(exchSim)
                            .setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
                    addOrder(newOrder);
                }
            }
        }

        for (Double key : amendMap.keySet()) {
            double price = key;
            if (price > 0) {
                long qty = amendMap.get(price);
                if (qty > 0) {
                    IOrder newOrder = new OrderBuilder().setOrderId(IdGenerator.genOrderID())
                            .setInstrumentId(this.instrumentId)
                            .setLimitPrice(price)
                            .setSide(isBid ? Side.BUY : Side.SELL)
                            .setOrderType(OrdType.LIMIT)
                            .setOrderQty(qty)
                            .setTimeInForce(0).setTrader(exchSim)
                            .setTimeStampMilliSec(System.currentTimeMillis()).createOrder();
                    addOrder(newOrder);
                }
            }
        }
        estimateQueuePosition();
    }

    @Override
    public void onMarketTrade(MarketTradeUpdate event) {
        if (event.getLastPx() <= this.getBestPrice(Side.BUY)) {
            estimateQueuePositionBid(event.getLastQty());
        }

        if (event.getLastPx() >= this.getBestPrice(Side.SELL)) {
            estimateQueuePositionAsk(event.getLastQty());
        }
    }


    @Override
    public boolean addOrder(IOrder order) {
        boolean success = false;
        if (order.getSide() == Side.BUY) {
            success = bidOrders.add(order);
        } else {
            success = askOrders.add(order);
        }
        if (success) {
            insertMarketByPrice(order);
            match();
            estimateQueuePosition();
        } else {
            logger.error("Failed to add order " + order);
        }
        return success;
    }

    @Override
    public boolean removeOrder(IOrder order) {
        boolean success = false;
        if (order.getSide() == Side.BUY) {
            success = bidOrders.remove(order);
        } else {
            success = askOrders.remove(order);
        }
        if (success) {
            eraseMarketByPrice(order);
            order.cancel();
        } else {
            logger.error("Failed to remove order " + order);
        }
        return success;
    }

    @Override
    public double getBestPrice(Side side) {
        if (side == Side.BUY) {
            if (bidOrders.size() > 0)
                return bidOrders.peek().getPrice();
            else return -1;
        } else {
            if (askOrders.size() > 0)
                return askOrders.peek().getPrice();
            else return -1;
        }
    }

    @Override
    public TopOfBook getTopOfBook(Side side) {
        double bestPx = getBestPrice(side);
        long liquidity = getLiquidityAtPrice(side, bestPx);
        return new TopOfBook(bestPx, liquidity);
    }

    @Override
    public Set<IOrder> getOrdersAtPrice(Side side, double px) {
        return side == Side.BUY ? bidMarketByPrice.get(px) : askMarketByPrice.get(px);
    }

    @Override
    public long getLiquidityAtPrice(Side side, double px) {
        return getOrdersAtPrice(side, px).stream().map(x -> x.getOpenQty()).collect(Collectors.summingLong(Long::longValue));
    }


    private boolean match() {
        while (true) {
            if (bidOrders.size() == 0 || askOrders.size() == 0) {
                return true;
            }
            IOrder bidOrder = bidOrders.peek();
            IOrder askOrder = askOrders.peek();
            if (bidOrder.getOrderType() == OrdType.MARKET
                    || askOrder.getOrderType() == OrdType.MARKET
                    || (bidOrder.getPrice() >= askOrder.getPrice())) {
                doMatch(bidOrder, askOrder);
                if (bidOrder.isClosed()) {
                    removeOrder(bidOrder);
                }
                if (askOrder.isClosed()) {
                    removeOrder(askOrder);
                }
            } else
                return true;
        }
    }

    private void doMatch(IOrder bid, IOrder ask) {
        double price;
        if (bid.getOrderType() == OrdType.MARKET) {
            price = ask.getPrice();
        } else if (ask.getOrderType() == OrdType.MARKET) {
            price = bid.getPrice();
        } else {
            price = ask.getOrderType() == OrdType.LIMIT ? ask.getPrice() : bid.getPrice();
        }
        long quantity = bid.getOpenQty() >= ask.getOpenQty() ? ask.getOpenQty() : bid.getOpenQty();
        bid.execute(price, quantity);
        ask.execute(price, quantity);
    }

    private void insertMarketByPrice(IOrder order) {
        if (order.getSide() == Side.BUY) {
            bidMarketByPrice.putIfAbsent(order.getPrice(), new HashSet<IOrder>());
            bidMarketByPrice.get(order.getPrice()).add(order);
        } else {
            askMarketByPrice.putIfAbsent(order.getPrice(), new HashSet<IOrder>());
            askMarketByPrice.get(order.getPrice()).add(order);
        }
    }

    private void eraseMarketByPrice(IOrder order) {
        if (order.getSide() == Side.BUY) {
            bidMarketByPrice.get(order.getPrice()).remove(order);
        } else {
            askMarketByPrice.get(order.getPrice()).remove(order);
        }
    }

    private void estimateQueuePositionBid(long lastQty) {
        Queue<IOrder> copy = new PriorityQueue<>(bidOrders);
        int pos = 0;
        while (!copy.isEmpty()) {
            IOrder o = copy.poll();
            if (o.getTrader().equals(exchSim)) {
                pos += o.getOpenQty();
            } else {
                o.setQueuePos(pos - lastQty);
                pos += o.getOpenQty();
            }
        }
    }

    private void estimateQueuePositionAsk(long lastQty) {
        Queue<IOrder> copy = new PriorityQueue<>(askOrders);
        int pos = 0;
        while (!copy.isEmpty()) {
            IOrder o = copy.poll();
            if (o.getTrader().equals(exchSim)) {
                pos += o.getOpenQty();
            } else {
                o.setQueuePos(pos - lastQty);
                pos += o.getOpenQty();
            }
        }
    }

    private void estimateQueuePosition() {
        estimateQueuePositionBid(0);
        estimateQueuePositionAsk(0);
    }

    private IOrder findOrder(Side side, String orderId) {
        Queue<IOrder> orders = side == Side.BUY ? bidOrders : askOrders;
        Optional<IOrder> opt = orders.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
        if (opt.isPresent())
            return opt.get();
        else
            return null;
    }
}
