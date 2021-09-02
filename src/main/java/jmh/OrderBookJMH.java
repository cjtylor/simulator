package jmh;


import com.exchange.simulator.Exchange;
import com.exchange.simulator.IExchange;
import com.exchange.simulator.marketdata.MarketDataProvider;
import com.exchange.simulator.order.IOrder;
import com.exchange.simulator.order.OrdType;
import com.exchange.simulator.order.OrderBuilder;
import com.exchange.simulator.order.Side;
import com.exchange.simulator.util.IdGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *  buy order: random price 90 - 102
 *  sell order: random price 98 - 110
 *  let them randomly match
 *
 *  @author Tylor
 */
@State(Scope.Thread)
public class OrderBookJMH {

    private IExchange exchange;
    private List<IOrder> buyOrders = new ArrayList<>();
    private List<IOrder> sellOrders = new ArrayList<>();
    private String symbol = "TestSymbol";

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OrderBookJMH.class.getSimpleName())
                .warmupIterations(2)
                .forks(1)
                .measurementIterations(3)
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init(){
        for(int i = 0; i< 1000; i++){
            buyOrders.add(generateRandomBuyOrder());
        }
        for(int i = 0; i< 1000; i++){
            sellOrders.add(generateRandomSellOrder());
        }
    }

    @Benchmark
    public void run() throws InterruptedException {
        exchange = new Exchange(new MarketDataProvider());
        for(int i = 0; i< 1000; i++){
            exchange.newOrder(buyOrders.get(i));
            exchange.newOrder(sellOrders.get(i));
        }
        exchange = null;
    }

    private IOrder generateRandomBuyOrder() {
        int maxPx = 102;
        int minPx = 90;
        double randomPx = random(minPx, maxPx);
        IOrder order = new OrderBuilder()
                .setOrderId(IdGenerator.genOrderID())
                .setInstrumentId(symbol)
                .setLimitPrice(randomPx)
                .setSide(Side.BUY)
                .setOrderType(OrdType.LIMIT)
                .setOrderQty((int) random(1, 100))
                .setTimeInForce(0).setTrader("")
                .setTimeStampMilliSec(System.currentTimeMillis())
                .createOrder();
        return order;
    }

    private IOrder generateRandomSellOrder() {
        int maxPx = 110;
        int minPx = 98;
        double randomPx = random(minPx, maxPx);
        IOrder order = new OrderBuilder()
                .setOrderId(IdGenerator.genOrderID())
                .setInstrumentId(symbol)
                .setLimitPrice(randomPx)
                .setSide(Side.SELL)
                .setOrderType(OrdType.LIMIT)
                .setOrderQty(random(1, 100))
                .setTimeInForce(0)
                .setTrader("")
                .setTimeStampMilliSec(System.currentTimeMillis())
                .createOrder();
        return order;
    }


    private int random(int min, int max) {
        double randomFactor = Math.random();
        double res = randomFactor * (max - min) + min;
        return (int) res;
    }

}
