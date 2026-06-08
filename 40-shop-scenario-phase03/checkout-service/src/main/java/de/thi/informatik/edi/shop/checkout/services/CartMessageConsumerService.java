package de.thi.informatik.edi.shop.checkout.services;

import java.net.UnknownHostException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thi.informatik.edi.shop.checkout.services.messages.ArticleAddedToCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.CartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.CreatedCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.DeleteArticleFromCartMessage;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Service
public class CartMessageConsumerService implements MessageConsumerService.MessageConsumerServiceHandler {
	
	private static Logger logger = LoggerFactory.getLogger(CartMessageConsumerService.class);

	@Value("${kafka.cartTopic:cart}")
	private String topic;
	
	private MessageConsumerService consumer;
	private Flux<CartMessage> flux;

	public CartMessageConsumerService(@Autowired MessageConsumerService consumer) {
		this.consumer = consumer;
	}

	private CartMessage apply(ConsumerRecord<String, String> record) {
		String value = record.value();
		logger.info("Received message " + value);
		try {
			CartMessage message = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.readValue(value, CartMessage.class);
			return message;
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new CartMessage();
	}

	@PostConstruct
	private void init() {
		Flux<ConsumerRecord<String, String>> register = this.consumer.register(topic, this);
		// Umwandeln von String in ein passendes Objekt vom Typ ArticleAddedToCartMessage,
		// DeleteArticleFromCartMessage oder CreatedCartMessage
		this.flux = register.map(this::apply); // private Flux<CartMessage> flux;
	}

	public Flux<CreatedCartMessage> getCreatedCartMessages() {
		return this.flux
				.filter(obj -> obj instanceof CreatedCartMessage)
				.map(obj -> (CreatedCartMessage) obj);
	}
	public Flux<ArticleAddedToCartMessage> getArticleAddedToCartMessages() {
		return this.flux
				.filter(obj -> obj instanceof ArticleAddedToCartMessage)
				.map(obj -> (ArticleAddedToCartMessage) obj);
	}
	public Flux<DeleteArticleFromCartMessage> getDeleteArticleFromCartMessages() {
		return this.flux
				.filter(obj -> obj instanceof DeleteArticleFromCartMessage)
				.map(obj -> (DeleteArticleFromCartMessage) obj);
	}

	@Override
	public void handle(String topic, String key, String value) {
	}
}
