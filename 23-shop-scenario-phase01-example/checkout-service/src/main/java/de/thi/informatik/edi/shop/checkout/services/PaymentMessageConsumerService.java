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

@Service
public class PaymentMessageConsumerService implements MessageConsumerService.MessageConsumerServiceHandler {

	private static Logger logger = LoggerFactory.getLogger(PaymentMessageConsumerService.class);

	@Value("${kafka.paymentTopic:payment}")
	private String topic;
	
	private MessageConsumerService consumer;
	private ShoppingOrderService orders;

	public PaymentMessageConsumerService(@Autowired ShoppingOrderService orders, @Autowired MessageConsumerService consumer) {
		this.orders = orders;
		this.consumer = consumer;
	}

	@PostConstruct
	private void init() {
		this.consumer.register(topic, this);
	}

	@Override
	public void handle(String topic, String key, String value) {
		logger.info("Received message " + value);
		try {
			PaymentMessage message = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(value, PaymentMessage.class);
			logger.info("Update order " + message.getOrderRef());
			if("PAYED".equals(message.getStatus())) {
				this.orders.updateOrderIsPayed(message.getOrderRef());
			} else if("PAYABLE".equals(message.getStatus())) {
				logger.info("Ignore status change " + message.getStatus() + " for order " + message.getOrderRef() + " and payment " + message.getId());
			} else {
				logger.info("Unknown status change " + message.getStatus() + " for order " + message.getOrderRef() + " and payment " + message.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
