package de.thi.informatik.edi.shop.shopping.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thi.informatik.edi.shop.shopping.services.messages.ShippingMessage;

@Service
public class ShippingMessageConsumerService extends MessageConsumerService {
	private PickedMessagePublisherService picked;
	
	public ShippingMessageConsumerService(PickedMessagePublisherService picked) {
		this.picked = picked;
	}
    
    protected void handle(String key, String msg) {
    	ShippingMessage shipping = this.fromMessage(msg);
    	shipping.getItems().forEach(el -> {
    		this.picked.handle(el.getArticle(), el.getCount());
    	});
    }

    private ShippingMessage fromMessage(String value) {
        try {
            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(value, ShippingMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new ShippingMessage(); // create empty message-instance, null is not the best solution
    }

    @Override
    protected String getClientId() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName() + "-shipping";
    }

    @Override
    protected String getTopic() {
        return "shipping";
    }
}
