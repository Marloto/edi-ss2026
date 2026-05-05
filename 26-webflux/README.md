Subscribe für eine Topic

```bash
docker compose exec mosquitto mosquitto_sub -h localhost -t test -v
```

Publish einer Nachricht in einem Topic

```bash
docker compose exec mosquitto mosquitto_pub -h localhost -t products -m "1,Apfel"
docker compose exec mosquitto mosquitto_pub -h localhost -t products -m "2,Banane"
docker compose exec mosquitto mosquitto_pub -h localhost -t products -m "3,Orange"
docker compose exec mosquitto mosquitto_pub -h localhost -t sales -m "1,3"
docker compose exec mosquitto mosquitto_pub -h localhost -t sales -m "2,5"
docker compose exec mosquitto mosquitto_pub -h localhost -t sales -m "3,1"
```