package de.thi.informatik.edi.shop.shopping.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PickedMessagePublisherService {
    private IotMessageBrokerService broker;

    public PickedMessagePublisherService(@Autowired IotMessageBrokerService broker) {
        this.broker = broker;
    }

    public void handle(UUID id, int value) {
        broker.publish("article/" + id.toString() + "/picked", String.valueOf(value));
    }

}
