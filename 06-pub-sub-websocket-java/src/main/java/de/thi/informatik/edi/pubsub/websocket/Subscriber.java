package de.thi.informatik.edi.pubsub.websocket;

public interface Subscriber {
    void onEvent(Event event);
}
