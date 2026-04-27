package de.thi.informatik.edi.shop.checkout.services;

import java.net.UnknownHostException;

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

@Service
public class CartMessageConsumerService implements MessageConsumerService.MessageConsumerServiceHandler {
	
	private static Logger logger = LoggerFactory.getLogger(CartMessageConsumerService.class);

	@Value("${kafka.cartTopic:cart}")
	private String topic;
	
	private MessageConsumerService consumer;
	private ShoppingOrderService orders;

	public CartMessageConsumerService(@Autowired ShoppingOrderService orders, @Autowired MessageConsumerService consumer) {
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
			CartMessage message = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(value, CartMessage.class);
			if(message instanceof ArticleAddedToCartMessage) {
				logger.info("Article added to cart " + ((ArticleAddedToCartMessage) message).getId());
				this.orders.addItemToOrderByCartRef(
						((ArticleAddedToCartMessage) message).getId(),
						((ArticleAddedToCartMessage) message).getArticle(),
						((ArticleAddedToCartMessage) message).getName(),
						((ArticleAddedToCartMessage) message).getPrice(),
						((ArticleAddedToCartMessage) message).getCount());
			} else if(message instanceof DeleteArticleFromCartMessage) {
				logger.info("Article removed from cart " + ((DeleteArticleFromCartMessage) message).getId());
				this.orders.deleteItemFromOrderByCartRef(
						((DeleteArticleFromCartMessage) message).getId(),
						((DeleteArticleFromCartMessage) message).getArticle());
			} else if(message instanceof CreatedCartMessage) {
				logger.info("Cart created " + ((CreatedCartMessage) message).getId());
				this.orders.createOrderWithCartRef(((CreatedCartMessage) message).getId());

			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
