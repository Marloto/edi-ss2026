Start everything with docker-compose up, select one connector register call from register.http and use the following commands to consume and produce events (through db).

```bash
# Topic beobachten
docker compose exec kafka \
  /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic shop.public.orders \
  --from-beginning

docker compose exec postgres \
  psql -U postgres -d shop \
  -c "INSERT INTO orders (customer, product, amount) VALUES ('Alice', 'Laptop', 999);"
```

The minimal one should show:

```json
{
    "id": 4,
    "customer": "Alice",
    "product": "Laptop",
    "amount": 999.0,
    "created_at": 1776688273737535,
    "__deleted": "false",
    "__op": "c",
    "__ts_ms": 1776688273932
}
```