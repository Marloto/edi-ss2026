Create topic, check with details so see how partitions are mapped to leaders

```bash
docker compose exec kafka1 \
  /opt/kafka/bin/kafka-topics.sh \
  --create --topic bestellungen \
  --partitions 3 \
  --replication-factor 3 \
  --bootstrap-server kafka1:29092
```

```bash
docker compose exec kafka1 \
  /opt/kafka/bin/kafka-topics.sh \
  --describe --topic bestellungen \
  --bootstrap-server kafka1:29092
```

Stop one kafka node (docker compose down kafka2), check describe again, one partition should get another leader.