package de.thi.informatik.edi.pubsub;

public class MessageEvent extends Event {

    private String value;
    public MessageEvent(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public String toString() {
        return "MessageEvent [value=" + value + "]";
    }
}
