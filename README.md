## Intro
A simple implementation of simulator exchange.

## The functionalities of exchange simulator
1) Place new order on simulator
2) Cancel an existing order
3) Match orders on exchange, when condition is fulfilled.
4) Provide simple market data simulation,see `IOrderBook.onMarketDataUpdate` , `IOrderBook.onMarketTrade`


## Design and assumptions
1) This simulator does not consider underlying transportation protocol - whether it's FIX message or other.
2) It provides minimum order validations, assuming messaging gateway has already validated the order. 


## Key data structures used in exchange simulator
A priority queue is used, in order to support price / time priority. See `OrderComparator`

## How to compile and build your project
Install maven and issue the following command.
```
$ mvn clean package
```


## How your code is tested?
Unit test is included, see
`OrderComparatorTest`
`OrderBookTest`
`ExchangeTest`
`MarketDataSimulationTest`

How to run unit test
```
$ mvn clean package test
```
small JMH test included - see `jmh.OrderBookJMH`







 