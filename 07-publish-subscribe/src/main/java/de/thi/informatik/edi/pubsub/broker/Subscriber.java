package de.thi.informatik.edi.pubsub.broker;

public interface Subscriber {
    void onEvent(Event event);
}
