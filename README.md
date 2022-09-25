# fix-me

**fix-me** is a simple router that forward messages in [FIX](https://en.wikipedia.org/wiki/Financial_Information_eXchange) format between brokers and markets.  

The router authenticate each clients (markets and brokers) by assigning them an ID, and forward messages to clients with their IDs.  
A reconnect token is sent to a client when it login, it can be used to reconnect with the same assigned ID, and to receive pending messages when reconnecting.

The messages are sent in the **FIX** format, but they do not follow any specification, and have arbitrary MsgTypes and Tags, as this was *not* the goal of the project, which was the implementation of a server with multiple threads.

## Usage

[Maven](https://maven.apache.org/) and Java 17 is required.

```bash
mvn package
# Three executables are generated
java -jar router/target/router-1.0-jar-with-dependencies.jar
java -jar market/target/market-1.0-jar-with-dependencies.jar
java -jar broker/target/broker-1.0-jar-with-dependencies.jar
```

Once all three executables are started, you can use the broker to send message to the market trough the router by entering a command, e.g:

```bash
# ---- Commands
# buy {market} {instrument} {quantity} {price}: Send a Buy message
# sell {market} {instrument} {quantity} {price}: Send a Sell message
# quit,exit: Close the Broker
# ----
# You can find a market ID and it's instruments when you start a market
buy {marketId} {instrumentId} 42 42
```

An optional database can be started to save the transactions of each markets:

```bash
cp database/.env.example database/.env
docker compose up
```

Will open a Postgres database on port 5442, and an [adminer](https://www.adminer.org/) view on port 5050 to see your database.  
To connect to the database you can set the credentials in the ``/database/.env`` file, or use the default one: ``database:admin/admin@fix-me``.
