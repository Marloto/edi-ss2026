package de.thi.informatik.edi.pubsub;

public interface Subscriber {
    void onEvent(Event event);
}
