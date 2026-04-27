package de.thi.informatik.edi.shop.shopping.services;

import de.thi.informatik.edi.shop.shopping.services.messages.StockMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StockChangedMessageProducerService {
    private MessageProducerService messages;

    @Value("${kafka.stockTopic:stock-changes}")
    private String topic;

    public StockChangedMessageProducerService(@Autowired MessageProducerService messages) {
        this.messages = messages;
    }

    public void handle(String article, int count) {
    	this.messages.send(topic, article, new StockMessage(count));
    }
}
