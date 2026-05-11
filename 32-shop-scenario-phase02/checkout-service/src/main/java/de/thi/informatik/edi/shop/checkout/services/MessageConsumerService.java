package de.thi.informatik.edi.shop.checkout.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class MessageConsumerService {

	private Flux<ConsumerRecord<String, String>> flux;

	public static interface MessageConsumerServiceHandler {
		public void handle(String topic, String key, String value);
	}

	private static Logger logger = LoggerFactory.getLogger(ShippingMessageConsumerService.class);
	
	@Value("${kafka.servers:localhost:9092}")
	private String servers;
	@Value("${kafka.group:checkout}")
	private String group;

	private Map<String, MessageConsumerServiceHandler> topics;
	private KafkaConsumer<String, String> consumer;
	private boolean running;
	private boolean hasToUpdate;
	private TaskExecutor executor;
	
	public MessageConsumerService(@Autowired TaskExecutor executor) {
		this.executor = executor;
		this.running = false;
		this.topics = new HashMap<>();
	}
	
	@PostConstruct
	private void init() throws UnknownHostException {
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName() + "-consumer");
		config.put("bootstrap.servers", servers);
		config.put("group.id", group);
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", StringDeserializer.class.getName());
		logger.info("Connect to " + servers + " as " + config.getProperty("client.id") + "@" + group);
		this.consumer = new KafkaConsumer<>(config);
	}

	private void handle(ConsumerRecord<String, String> el) {
		MessageConsumerServiceHandler handler = this.topics.get(el.topic());
		if(handler != null) {
			handler.handle(el.key(), el.value(), el.value());
		}
	}

	private void start() {
		if(!this.running) {
			logger.info("Start consumer thread");
			// Senke erzeugen, in die wir Messages vom Broker emittieren
			Sinks.Many<ConsumerRecord<String, String>> many = Sinks.many().multicast().onBackpressureBuffer();

			this.running = true;
			this.executor.execute(() -> {
				while (running) {
					if(hasToUpdate) {
						logger.info("Update subscription to " + this.topics.keySet());
						consumer.subscribe(new ArrayList<>(this.topics.keySet()));
						hasToUpdate = false;
					}
					try {
						if(consumer.subscription().size() > 0) {
							ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
							records.forEach(el -> {
								many.tryEmitNext(el); // Messages an Senke übergeben
								handle(el);
							});
							consumer.commitSync();
						} else {
							Thread.sleep(1000);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.flux = many.asFlux();  // muss dafür ergänzt werden: private Flux<ConsumerRecord<String, String>> flux;
		}
	}

	/*
	 import reactor.core.publisher.Flux;
	 import reactor.core.publisher.Sinks;

	 oder cursor an die rote stelle und alt + enter -> import class
	 */

	public synchronized Flux<ConsumerRecord<String, String>> register(String topic, MessageConsumerServiceHandler handler) {
		this.topics.put(topic, handler);
		this.hasToUpdate = true;
		logger.info("Add subscribe for " + topic);
		this.start();
		return this.flux.filter(record -> record.topic().equals(topic));
	}

	@PreDestroy
	private void shutDown() {
		this.running = false;
	}
}
