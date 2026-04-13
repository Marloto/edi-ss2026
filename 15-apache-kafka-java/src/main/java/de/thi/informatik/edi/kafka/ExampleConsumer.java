package de.thi.informatik.edi.kafka;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleConsumer {
	private static Logger logger = LoggerFactory.getLogger(ExampleProducer.class);
	
	public static void main(String[] args) throws Exception {
		final String topic = "test-topic";

		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", "localhost:9092");
		config.put("group.id", "foo");

		// Information notwendig, wie Key und Value in byte[] überführt werden
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", StringDeserializer.class.getName());

        try(Consumer<String, String> consumer = new KafkaConsumer<>(config)) {
            consumer.subscribe(List.of(topic));
            while(true) {
                ConsumerRecords<String, String> records = consumer
                        .poll(Duration.ofMillis(2000));
                records.forEach(record -> {
                    logger.info("Received at topic {} msg {} with key {}",
                            record.topic(),  record.value(),  record.key());
                });
                consumer.commitSync();
            }
        }
	}
}