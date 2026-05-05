Subscribe für eine Topic

```bash
docker compose exec mosquitto mosquitto_sub -h localhost -t servers/# -v
```

Publish einer Nachricht in einem Topic

```bash
docker compose exec mosquitto mosquitto_pub -h localhost -t example -m "Hello, World"
```