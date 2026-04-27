# Shop Example

Shop setup with shopping service, checkout service, payment service and warehouse service. The shop can be used to browse available products and place articles on a shopping cart, the checkout service can be used to order the created cart and the payment service can be used to pay this order. The warehouse service finally allows to ship items. By default, all services are not fully implemented. The communication between each service is not realized and has to be added.

## Prepare

In order to run this example, some additional ressources has to be loaded. This can be done before by using:

```
docker compose pull
```

## Run

- **First Time**, start with: `docker compose up -d init-kafka` (creates topics)
  - This will create topics, which can be used for your examples
  - Check with `docker compose logs init-kafka`, result should be:

```
...
init-kafka-1  | Successfully created the following topics:
init-kafka-1  | article
init-kafka-1  | cart
init-kafka-1  | order
init-kafka-1  | payment
init-kafka-1  | shipping
init-kafka-1  | stock-changes
```

- **Start all containers** for development: `docker compose up -d`
- Start only Kafka / Zookeeper: `docker compose up -d kafka zookeeper`
- Start only Load Balancer: `docker compose up -d nginx`
- Start only Adapters: `docker compose up -d warehouse-sensor warehouse-adapter`
- Stop: `docker compose stop`
- Remove all containers: `docker compose down`
- Remove all containers, incl. collected data: `docker compose down -v`, e.g., if you face any kafka connection issues use this (mostly because of changed networks)

## Usage

- Import Project **Shopping Service**, **Checkout Service**, **Payment Service** or. **Warehouse Service** into your IDE
- Run as Java Application by using `Application.java` for **Shopping Service**, **Checkout Service** and **Payment Service**
- Got to browser and use http://localhost:8080/shopping/shop
- Select some articles and continue to checkout
- _Tip: Pressing Alt+Shift+F in checkout will fill up the form with dummy data_
- You will see missing items
- Proceed to payment, which will get stuck at `Please wait...`

## Utils

- Kafka UI: http://localhost:8090
- MongoDB UI: http://localhost:8091 (Username: admin, Password: changeme)

_CLI Commands for Kafka and Mosquitto_

```
docker compose exec kafka kafka-console-consumer.sh --whitelist 'article|order|shipping|stock-changes|payment|cart' --bootstrap-server localhost:9092
docker compose exec mosquitto mosquitto_sub -h localhost -t '#' -v
```

## Task (1)

- Review source code of **Shopping Service** and **Checkout Service**
- Both has an layered architecture containing REST Controller, Service and Model / Repository
- **Don't change REST Controller or Model / Repository**
- Shopping Service has to inform the checkout service about the shipping cart, thus, `CartService` is a good starting point
- You will find `addArticle`, `deleteArticle` and `cartFinished`
- Integrate Apache Kafka and produce messages related to add / delete or cart finished, pass the ID of your cart as reference
- Next step is to use messages from checkout service
- Go to `ShoppingOrderService` and find `createOrderWithCartRef`, `addItemToOrderByCartRef` and `deleteItemFromOrderByCartRef`
- Integrate Apache Kafka and consume messages related to your integration in shopping service and use the `ShoppingOrderService`

_Afterwards, go further and extend **Payment** and **Warehouse Service** as well._

_Note: If you want to serialize data as JSON, you can stick with Spring._

```java
// Imports
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
```

```java
// Serialize
static String asJsonString(final Object obj) {
  try {
    return new ObjectMapper().writeValueAsString(obj);
  } catch (Exception e) {
    throw new RuntimeException(e);
  }
}
```

```java
// Handle JSON-String
private void handle(String key, String value) {
  try {
    SomeMessage message = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(value, SomeMessage.class);
    // ... do something
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

## Task (2)

- Depending on programming language, open `warehouse-adapter` (NodeJS) or `warehouse-adapter-spring` (Java, maybe with Spring)
- This is an empty project intended to integrate from scratch
- The MQTT broker (reachable from `localhost:1883`) has a topic `article/<id>/stock`, e. g., `article/1a88e23c-56ce-4a17-8b5b-4d80df805792/stock` with integer values for current stock
- In addition there is an other topic for `article/<id>/picked`, e. g., `article/1a88e23c-56ce-4a17-8b5b-4d80df805792/picked`, which should contain an integer for picked / shipped items, the adapter might get this information from the Apache Kafka and forward this to the MQTT broker

_Picking has to be synchronized with **Warehouse Service**._

## Services

_Short description for all services in this project_

- **Shopping Service**: Main shop page with shopping cart and article management
- **Checkout Service**: Handles orders and allow to checkout
- **Payment Service**: Handle payment for orders
- **Warehouse Service**: Worker page for shipping orders
- **Warehouse Sensor Adapter**: Service for mapping stock changes from IoT broker to kafka and shippings from kafka to IoT broker (there is a NodeJS version as well as Spring, the Spring version is currently used)
- IoT Broker: MQTT service for warehouse sensor signals
- Apache Kafka / Zookeeper: Message queue as main broker between services
- Warehouse Sensor Service: Simulated sensors, refilling is automated
