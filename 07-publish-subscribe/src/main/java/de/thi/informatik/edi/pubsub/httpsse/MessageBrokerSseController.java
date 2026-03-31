package de.thi.informatik.edi.pubsub.httpsse;

import de.thi.informatik.edi.pubsub.broker.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/broker-sse")
public class MessageBrokerSseController {
    private final MessageBroker broker;
    private final Map<String, Topic> topics = new HashMap<>();

    private synchronized Topic getOrCreate(String topic) {
        Topic t = topics.get(topic);
        if(t == null) {
            t = new Topic(topic);
            topics.put(topic, t);
        }
        return t;
    }

    public MessageBrokerSseController(MessageBroker messageBroker) {
        this.broker = messageBroker;
    }

    @GetMapping("/subscribe/{topic}")
    public SseEmitter subscribe(@PathVariable String topic) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        Topic t = getOrCreate(topic);

        Subscriber sub = new Subscriber() {
            public void onEvent(Event event) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(event.toString()));
                } catch (IOException e) {
                    broker.unsubscribe(t, this);
                    throw new RuntimeException(e);
                }
            }
        };
        broker.subscribe(t, sub);

        emitter.onCompletion(() -> broker.unsubscribe(t, sub));
        emitter.onTimeout(() -> broker.unsubscribe(t, sub));

        return emitter;
    }

    @GetMapping("/publish/{topic}")
    public void publish(@PathVariable String topic, @RequestParam String message) {
        Topic t = getOrCreate(topic);
        this.broker.publish(t, new MessageEvent(message));
    }
}
