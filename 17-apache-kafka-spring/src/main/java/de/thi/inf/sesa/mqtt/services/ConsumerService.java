package de.thi.inf.sesa.mqtt.services;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ConsumerService {

    @Value("${kafka.servers:localhost:9092}")
    private String servers;
    @Value("${kafka.group:foo}")
    private String group;

    private final List<String> messages = new CopyOnWriteArrayList<>();


    @PostConstruct
    public void init() throws UnknownHostException {
        Properties config = new Properties();
        config.put("bootstrap.servers", servers);
        config.put("group.id", group);
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("key.deserializer", StringDeserializer.class.getName());
        config.put("value.deserializer", StringDeserializer.class.getName());
        config.put("auto.offset.reset", "earliest");

        Consumer<String, String> consumer = new KafkaConsumer<>(config);

        consumer.subscribe(List.of("test"));

        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                records.forEach(r -> messages.add(r.value()));
                consumer.commitSync();
            }
        }).start();
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

}