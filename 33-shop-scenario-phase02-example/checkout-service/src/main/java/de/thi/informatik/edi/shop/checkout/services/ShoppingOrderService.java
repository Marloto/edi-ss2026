package de.thi.informatik.edi.shop.checkout.services;

import java.util.Optional;
import java.util.UUID;

import de.thi.informatik.edi.shop.checkout.services.messages.ArticleAddedToCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.CreatedCartMessage;
import de.thi.informatik.edi.shop.checkout.services.messages.DeleteArticleFromCartMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.checkout.model.ShoppingOrder;
import de.thi.informatik.edi.shop.checkout.model.ShoppingOrderStatus;
import de.thi.informatik.edi.shop.checkout.repositories.ShoppingOrderRepository;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple3;

@Service
public class ShoppingOrderService {
    private static Logger logger = LoggerFactory.getLogger(ShoppingOrderService.class);
	private ShoppingOrderRepository orders;
	private ShoppingOrderMessageProducerService messages;
    private final CartMessageConsumerService cartMessages;
    private Sinks.Many<ShoppingOrder> sink;

    public ShoppingOrderService(ShoppingOrderRepository orders,
			ShoppingOrderMessageProducerService messages,
            CartMessageConsumerService cartMessages) {
		this.orders = orders;
		this.messages = messages;
        this.cartMessages = cartMessages;
    }
	
	@PostConstruct
	private void init() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.messages.orderChanged(sink.asFlux());
        this.cartMessages.getAddedToCartMessages().subscribe(message -> {
            logger.info("Article added to cart " + message.getId());
            this.addItemToOrderByCartRef(
                    message.getId(),
                    message.getArticle(),
                    message.getName(),
                    message.getPrice(),
                    message.getCount());
        });

        this.cartMessages.getDeletedFromCartMessages().subscribe(message -> {
            logger.info("Article removed from cart " + message.getId());
            this.deleteItemFromOrderByCartRef(
                    message.getId(),
                    message.getArticle());
        });

        this.cartMessages.getCreatedCartMessages().subscribe(message -> {
            logger.info("Cart created " + message.getId());
            this.createOrderWithCartRef(message.getId());
        });
	}
	
	public void addItemToOrderByCartRef(UUID cartRef, UUID article, String name, double price, int count) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.addItem(article, name, price, count);
		this.orders.save(order);
		
	}
	
	private Optional<ShoppingOrder> findByCartRef(UUID cartRef) {
		return this.orders.findByCartRef(cartRef);
	}

	public void deleteItemFromOrderByCartRef(UUID cartRef, UUID article) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.removeItem(article);
		this.orders.save(order);
	}

	public ShoppingOrder createOrderWithCartRef(UUID cartRef) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		this.orders.save(order);
		return order;
	}

	private ShoppingOrder getOrCreate(UUID cartRef) {
		Optional<ShoppingOrder> orderOption = this.findByCartRef(cartRef);
		ShoppingOrder order;
		if(orderOption.isEmpty()) {
			order = new ShoppingOrder(cartRef);
		} else {
			order = orderOption.get();
		}
		return order;
	}

	public void updateOrder(UUID id, String firstName, String lastName, String street, String zipCode,
			String city) {
		ShoppingOrder order = findById(id);
		order.update(firstName, lastName, street, zipCode, city);
		this.orders.save(order);
	}

	private ShoppingOrder findById(UUID id) {
		Optional<ShoppingOrder> optional = this.orders.findById(id);
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("Unknown order with id " + id.toString());
		}
		ShoppingOrder order = optional.get();
		//this.messages.orderChanged(order);
        this.sink.tryEmitNext(order);
		return order;
	}

	public void placeOrder(UUID id) {
		ShoppingOrder order = findById(id);
		if(order.getStatus().equals(ShoppingOrderStatus.CREATED)) {
			order.setStatus(ShoppingOrderStatus.PLACED);
			//this.messages.orderChanged(order);
            this.sink.tryEmitNext(order);
			this.orders.save(order);
		}
	}

	public ShoppingOrder find(UUID id) {
		return this.findById(id);
	}

	public void updateOrderIsPayed(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PLACED) {
			order.setStatus(ShoppingOrderStatus.PAYED);
			//this.messages.orderChanged(order);
            this.sink.tryEmitNext(order);
			this.orders.save(order);
		}
	}

	public void updateOrderIsShipped(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PAYED) {
			order.setStatus(ShoppingOrderStatus.SHIPPED);
			//this.messages.orderChanged(order);
            this.sink.tryEmitNext(order);
			this.orders.save(order);
		}
	}
}
