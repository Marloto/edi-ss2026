package de.thi.informatik.edi.shop.shopping.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class MessageProducerService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${kafka.servers:localhost:9092}")
	private String servers;

	private Producer<String, String> producer;

	public MessageProducerService() {
	}

	@PostConstruct
	private void init() throws UnknownHostException {
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", servers);
		config.put("acks", "all");
		config.put("key.serializer", StringSerializer.class.getName());
		config.put("value.serializer", StringSerializer.class.getName());
		producer = new KafkaProducer<>(config);
	}

	public void send(String topic, String key, Object data) {
		logger.info("Send (" + topic + ") with key: " + data);
		ProducerRecord<String, String> r = new ProducerRecord<>(topic, key, asJsonString(data));
		producer.send(r);
	}

	static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
