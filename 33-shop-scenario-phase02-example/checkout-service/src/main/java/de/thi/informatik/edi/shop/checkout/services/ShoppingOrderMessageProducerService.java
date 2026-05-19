package de.thi.informatik.edi.shop.checkout.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.checkout.model.ShoppingOrder;
import de.thi.informatik.edi.shop.checkout.services.messages.ShoppingOrderMessage;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

@Service
public class ShoppingOrderMessageProducerService {
	
	private static Logger logger = LoggerFactory.getLogger(ShoppingOrderMessageProducerService.class);
	
	private MessageProducerService messages;
	
	@Value("${kafka.orderTopic:order}")
	private String topic;

	public ShoppingOrderMessageProducerService(@Autowired MessageProducerService messages) {
		this.messages = messages;
	}

	public void orderChanged(Flux<ShoppingOrder> orders) {
		//logger.info("Send message for updating order " + order.getId());
		//this.messages.send(topic, order.getId().toString(), ShoppingOrderMessage.fromOrder(order));
        this.messages.register(orders.map(order -> Tuples.of(topic, order.getId().toString(), order)));
	}

}
