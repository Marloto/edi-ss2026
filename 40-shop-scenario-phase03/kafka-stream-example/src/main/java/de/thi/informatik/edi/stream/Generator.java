package de.thi.informatik.edi.stream;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import de.thi.informatik.edi.stream.JsonSerdes.JsonSerializer;
import de.thi.informatik.edi.stream.messages.ArticleAddedToCartMessage;
import de.thi.informatik.edi.stream.messages.PaymentMessage;
import de.thi.informatik.edi.stream.messages.ShippingItemMessage;
import de.thi.informatik.edi.stream.messages.ShippingMessage;
import de.thi.informatik.edi.stream.messages.ShoppingOrderItemMessage;
import de.thi.informatik.edi.stream.messages.ShoppingOrderMessage;

public class Generator {
	static final String CART_TOPIC = "cart";
	static final String SHIPPING_TOPIC = "shipping";
	static final String PAYMENT_TOPIC = "payment";
	static final String ORDER_TOPIC = "order";
	private static int DELAY = 1000;
	private static boolean muted = false;
	
	private static void print(String msg) {
		if(!muted) {
			System.out.println(msg);
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", "localhost:9092");
		config.put("acks", "all");
		config.put("key.serializer", StringSerializer.class.getName());
		config.put("value.serializer", JsonSerializer.class.getName());
		try(Producer<String, Object> producer = new KafkaProducer<>(config)) {
			while(true) {
				
				UUID cartId = UUID.randomUUID();
				UUID orderId = UUID.randomUUID();
				UUID paymentId = UUID.randomUUID();
				UUID shippingId = UUID.randomUUID();
				UUID articleId = UUID.fromString("433fe831-3401-4e28-b550-4b6efa425431");
				
				
				ProducerRecord<String, Object> record;
				record = new ProducerRecord<>(CART_TOPIC, cartId.toString(), 
						new ArticleAddedToCartMessage(
								articleId, "1kg Äpfel", 1, 3.99));
				producer.send(record, (metadata, e) -> print("Added to cart..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(ORDER_TOPIC, cartId.toString(), new ShoppingOrderMessage(
						orderId,
						"Erna",
						"Musterfrau",
						"Somewhere 123",
						"12345",
						"Exampletown",
						"CREATED", 
						3.99,
						new ShoppingOrderItemMessage(articleId, "1kg Äpfel", 1, 3.99)
				));
				producer.send(record, (metadata, e) -> print("Order created..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(ORDER_TOPIC, orderId.toString(), new ShoppingOrderMessage(
						orderId,
						"Erna",
						"Musterfrau",
						"Somewhere 123",
						"12345",
						"Exampletown",
						"PLACED", 
						3.99,
						new ShoppingOrderItemMessage(articleId, "1kg Äpfel", 1, 3.99)
				));
				producer.send(record, (metadata, e) -> print("Order placed..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(PAYMENT_TOPIC, orderId.toString(), new PaymentMessage(
						paymentId,
						orderId,
						"PAYABLE",
						"CREATED" 
				));
				producer.send(record, (metadata, e) -> print("Payment payable..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(PAYMENT_TOPIC, orderId.toString(), new PaymentMessage(
						paymentId,
						orderId,
						"PAYED",
						"PAYABLE" 
				));
				producer.send(record, (metadata, e) -> print("Payment payed..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(ORDER_TOPIC, orderId.toString(), new ShoppingOrderMessage(
						orderId,
						"Erna",
						"Musterfrau",
						"Somewhere 123",
						"12345",
						"Exampletown",
						"PAYED", 
						3.99,
						new ShoppingOrderItemMessage(articleId, "1kg Äpfel", 1, 3.99)
				));
				producer.send(record, (metadata, e) -> print("Order marked as payed..."));
				
				Thread.sleep(DELAY);
				
				record = new ProducerRecord<>(SHIPPING_TOPIC, orderId.toString(), new ShippingMessage(
						"SHIPPED",
						shippingId,
						orderId,
						new ShippingItemMessage(articleId, 1)
				));
				producer.send(record, (metadata, e) -> print("Shipping done..."));
			}
		}
	}
}
