package de.thi.informatik.edi.kafka;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleProducer {
	private static Logger logger = LoggerFactory.getLogger(ExampleProducer.class);
	public static void main(String[] args) throws Exception {
		final String topic = "test-topic";
		
		Properties config = new Properties();
		config.put(CommonClientConfigs.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
		config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
		config.put("acks", "all");
		config.put("key.serializer", StringSerializer.class.getName());
		config.put("value.serializer", StringSerializer.class.getName());
		config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
		config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		config.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"ashah\";");
		try(Producer<String, String> producer = new KafkaProducer<>(config)) {			
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, "test", "abc");
			Future<RecordMetadata> future = producer.send(record);
			RecordMetadata metadata = future.get();
			logger.info(metadata.toString());
		}
	}
}