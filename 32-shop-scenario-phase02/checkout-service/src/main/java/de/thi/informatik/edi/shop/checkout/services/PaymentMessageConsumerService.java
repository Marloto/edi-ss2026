package de.thi.informatik.edi.shop.checkout.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thi.informatik.edi.shop.checkout.services.messages.PaymentMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Flux;

@Service
public class PaymentMessageConsumerService implements MessageConsumerService.MessageConsumerServiceHandler {

	private static Logger logger = LoggerFactory.getLogger(PaymentMessageConsumerService.class);

	@Value("${kafka.paymentTopic:payment}")
	private String topic;
	
	private MessageConsumerService consumer;
	private Flux<PaymentMessage> messages;

	public PaymentMessageConsumerService(@Autowired MessageConsumerService consumer) {
		this.consumer = consumer;
	}

	private PaymentMessage apply(ConsumerRecord<String, String> record) {
		String value = record.value();
		logger.info("Received message " + value);
		try {
			return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(value, PaymentMessage.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new PaymentMessage();
	}

	@PostConstruct
	private void init() {
		this.messages = this.consumer.register(topic, this).map(this::apply);
	}

	public Flux<PaymentMessage> getMessages() {
		return messages;
	}

	@Override
	public void handle(String topic, String key, String value) {
	}
}
