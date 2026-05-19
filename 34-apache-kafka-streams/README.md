# Apache Kafka Streams - Hello World

## Start Kafka

```bash
docker compose up -d
```

## Create Topic

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --topic hello-world \
  --partitions 1 --replication-factor 1
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --topic hello-world-answer \
  --partitions 1 --replication-factor 1
```

## Publish Messages

```bash
docker compose exec -it kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic hello-world
```

Type a message and press Enter. Each line is one message. `Ctrl+C` to stop.

## Consume Messages

```bash
docker compose exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic hello-world-answer \
  --from-beginning
```

## Stop Kafka

```bash
docker compose down
```
