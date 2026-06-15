# Apache Flink Hello World

## Lokal ausführen (IntelliJ oder Maven)

```bash
mvn compile exec:exec \
  -Dexec.executable="java" \
  -Dexec.args="-cp %classpath de.thi.informatik.edi.flink.hello.HelloWorldJob"
```

## Cluster starten

```bash
docker compose up -d
```

Flink Web UI: http://localhost:8081

## JAR bauen (für Cluster)

```bash
mvn package -Pcluster
```

## Job deployen

```bash
# HelloWorldJob
flink run -m localhost:8081 target/apache-flink-examples-0.1.jar

# KafkaSourceJob (bootstrap-server als Argument)
flink run -m localhost:8081 \
  --class de.thi.informatik.edi.flink.hello.KafkaSourceJob \
  target/apache-flink-examples-0.1.jar \
  kafka:29092
```

Alternativ per Web UI: http://localhost:8081 → Submit New Job → JAR hochladen. _Für HelloWorldJob keine Parameter notwendig, Ausgaben im Log über Docker zu finden._