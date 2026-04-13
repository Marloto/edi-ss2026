package de.thi.informatik.edi.pubsub;

import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageBroker {
    private Map<Topic, List<Subscriber>> subscribers = new HashMap<>();

    void subscribe(Topic topic, Subscriber subscriber) {
        List<Subscriber> list = subscribers.get(topic);
        if(list == null) {
            list = new ArrayList<>();
            subscribers.put(topic, list);
        }
        list.add(subscriber);
    }

    void unsubscribe(Topic topic, Subscriber subscriber) {
        List<Subscriber> list = subscribers.get(topic);
        if(list != null) {
            list.remove(subscriber);
            if(list.isEmpty()) {
                subscribers.remove(topic);
            }
        }
    }

    void publish(Topic topic, Event event) {
        List<Subscriber> list = subscribers.get(topic);
        if(list != null) {
            for(Subscriber subscriber : list) {
                subscriber.onEvent(event);
            }
        }
    }

    public static class TempEvent extends Event {
        private double value;
        public TempEvent(double value) {
            this.value = value;
        }
        public double getValue() {
            return value;
        }

        public String toString() {
            return "TempEvent [value=" + value + "]";
        }
    }

    public static void main(String[] args) {
        MessageBroker broker = new MessageBroker();
        Topic topic1 = new Topic("temperature-building-d");
        Topic topic2 = new Topic("temperature-building-g");
        Subscriber sub1 = (event) -> System.out.println("A: " + event);
        broker.subscribe(topic1, sub1);
        broker.subscribe(topic2, sub1);
        broker.subscribe(topic1, (event) -> System.out.println("B: " + event));
        broker.subscribe(topic1, (event) -> System.out.println("C: " + event));
        broker.publish(topic1, new TempEvent(21));
        broker.publish(topic2, new TempEvent(19));
    }
}
