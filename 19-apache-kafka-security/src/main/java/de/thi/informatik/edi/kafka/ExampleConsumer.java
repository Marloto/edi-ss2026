package de.thi.informatik.edi.kafka;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleConsumer {
	private static Logger logger = LoggerFactory.getLogger(ExampleProducer.class);
	private static boolean running;
	
	public static void main(String[] args) throws Exception {
		final String topic = "test-topic";

		Properties config = new Properties();
		config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
		config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
		config.put(CommonClientConfigs.GROUP_ID_CONFIG, "foo");
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", StringDeserializer.class.getName());
		config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
		config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		config.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"ashah\";");
		
		
		try(Consumer<String, String> consumer = new KafkaConsumer<>(config)) {
			consumer.subscribe(List.of(topic));
			running = true;
			new Thread(() -> {
				while (running) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(1));
					records.forEach(el -> logger.info(el.key() + ": " + el.topic()));
					consumer.commitSync();
				}				
			}).start();
			System.in.read();
			running = false;
		}
	}
}