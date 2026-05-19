package de.thi.informatik.edi.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Repartitioned;

import java.util.Properties;

/**
 * # Topic mit mehr Partitionen erstellen
 * kafka-topics --create --topic input-topic-multi \
 *   --bootstrap-server localhost:9092 \
 *   --partitions 4 \
 *   --replication-factor 1
 *
 * # Oder bestehendes Topic erweitern (falls möglich)
 * kafka-topics --alter --topic input-topic \
 *   --bootstrap-server localhost:9092 \
 *   --partitions 4
 * mvn exec:java -Dexec.mainClass="de.thi.informatik.edi.streams.RepartitionExperiment" -Dexec.args="PROZESS-1"
 * mvn exec:java -Dexec.mainClass="de.thi.informatik.edi.streams.RepartitionExperiment" -Dexec.args="PROZESS-2"
 *
 * # Remove if wrong partition
 * kafka-topics --list --bootstrap-server localhost:9092 | grep repartition-demo
 * kafka-topics --delete --topic repartition-demo-repartitioned-topic-repartition \
 *   --bootstrap-server localhost:9092
 *
 * docker compose exec kafka kafka-console-producer.sh --topic input-topic --bootstrap-server localhost:9092
 *
 * docker compose exec kafka kafka-console-consumer.sh \
 *   --topic repartition-demo-KSTREAM-AGGREGATE-STATE-STORE-0000000007-changelog \
 *   --bootstrap-server localhost:9092 \
 *   --from-beginning \
 *   --property "print.key=true" \
 *   --property "key.separator= → " \
 *   --value-deserializer "org.apache.kafka.common.serialization.LongDeserializer"
 */

public class RepartitionExperiment {
  public static void main(String[] args) {
    Properties config = new Properties();
    {
      config.put(StreamsConfig.APPLICATION_ID_CONFIG, "repartition-demo");
      config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      config.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 1);
      config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
      config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
      config.put(StreamsConfig.CLIENT_ID_CONFIG, "client-" + args[0]);
      config.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams-" + args[0]);
    }

    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> source = builder.stream("input-topic");

    // Logging vor Repartition
    source.peek((key, value) ->
      System.out.println("[" + args[0] + "] BEFORE repartition - Key: " + key +
        ", Value: " + value + ", Thread: " + Thread.currentThread().getName()));

    // Repartition basierend auf erstem Buchstaben
    KStream<String, String> repartitioned = source
      .selectKey((key, value) -> value.substring(0, 1)) // Neuer Key = erster Buchstabe
      .repartition(Repartitioned.as("repartitioned-topic"));

    // Logging nach Repartition
    repartitioned.peek((key, value) ->
      System.out.println("[" + args[0] + "] AFTER repartition - Key: " + key +
        ", Value: " + value + ", Thread: " + Thread.currentThread().getName()));

    // Weitere Verarbeitung
    repartitioned
      .groupByKey()
      .count()
      .toStream()
      .peek((key, count) ->
        System.out.println("[" + args[0] + "] COUNT - Key: " + key +
          ", Count: " + count + ", Thread: " + Thread.currentThread().getName()));

    KafkaStreams streams = new KafkaStreams(builder.build(), config);
    streams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }
}
