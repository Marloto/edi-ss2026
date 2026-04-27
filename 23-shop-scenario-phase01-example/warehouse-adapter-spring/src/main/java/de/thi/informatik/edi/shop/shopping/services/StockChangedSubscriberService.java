package de.thi.informatik.edi.shop.shopping.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class StockChangedSubscriberService {
    private IotMessageBrokerService broker;
    private StockChangedMessageProducerService target;

    private static final Pattern p = Pattern.compile("article/(.*?)/stock");

    public StockChangedSubscriberService(@Autowired IotMessageBrokerService broker,
    		@Autowired StockChangedMessageProducerService target) {
        this.broker = broker;
		this.target = target;
    }

    @PostConstruct
    private void init() {
    	this.broker.subscribe("article/+/stock", (key, data) -> {
    		Matcher matcher = p.matcher(key);
    		if(matcher.find()) {
    			int value = Integer.parseInt(data);
    			String article = matcher.group(1);
    			target.handle(article, value);
    		}
    	});
    }
}
