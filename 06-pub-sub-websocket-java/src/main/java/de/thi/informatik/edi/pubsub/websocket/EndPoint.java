package de.thi.informatik.edi.pubsub.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


@ServerEndpoint("/{topic}")
public class EndPoint implements Subscriber {

    // Subscribe: Wann?
    // -> sobald client aufruft -> also OnOpen
    // Publish im onMessage

    private static MessageBroker broker = new MessageBroker();

    private static final Map<String, Topic> topics = new HashMap<>();
    private Session session;
    private Topic topic;

    private synchronized  static Topic getOrCreate(String topic) {
        Topic t = topics.get(topic);
        if(t == null) {
            t = new Topic(topic);
            topics.put(topic, t);
        }
        return t;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("topic") String topic) throws IOException {
        this.topic = getOrCreate(topic);
        this.session = session;
        broker.subscribe(this.topic, this);
    }

    @OnMessage
    public void echo(String message, @PathParam("topic") String topic) {
        if (this.topic != null) {
            broker.publish(this.topic, new MessageEvent(message));
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, @PathParam("topic") String topic) {
        if (this.topic != null) {
            broker.unsubscribe(this.topic, this); // je nach deinem Broker-API
        }
    }

    @Override
    public void onEvent(Event event) {
        if(session.isOpen()) {
            session.getAsyncRemote().sendText(event.toString());
        } else {
            broker.unsubscribe(this.topic, this);
        }
    }
}