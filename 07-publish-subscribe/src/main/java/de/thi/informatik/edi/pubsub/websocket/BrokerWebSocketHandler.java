package de.thi.informatik.edi.pubsub.websocket;

import de.thi.informatik.edi.pubsub.broker.*;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class BrokerWebSocketHandler extends TextWebSocketHandler {

    private final MessageBroker broker;
    private Topic topic;
    private Subscriber sub;

    public BrokerWebSocketHandler(MessageBroker broker) {
        this.broker = broker;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = session.getUri().getPath();
        String topicName = path.substring(path.lastIndexOf('/') + 1);
        // topic aus URI extrahieren...

        sub = new Subscriber() {
            public void onEvent(Event event) {
                try {
                    session.sendMessage(new TextMessage(event.toString()));
                } catch (IOException e) {
                    broker.unsubscribe(topic, this);
                }
            }
        };
        broker.subscribe(topic, sub);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broker.unsubscribe(topic, sub);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        broker.publish(topic, new MessageEvent(message.getPayload()));
    }
}
