package de.thi.informatik.edi.pubsub.http;

import de.thi.informatik.edi.pubsub.broker.Event;
import de.thi.informatik.edi.pubsub.broker.MessageBroker;
import de.thi.informatik.edi.pubsub.broker.MessageEvent;
import de.thi.informatik.edi.pubsub.broker.Topic;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/broker") // -> http://localhost:8080/broker
public class MessageBrokerController {
    private final MessageBroker broker;

    private final Map<String, Topic> topics = new HashMap<>();

    private final Map<UUID, List<Event>> events = new HashMap<>();

    public MessageBrokerController(MessageBroker messageBroker) {
        this.broker = messageBroker;
    }

    private synchronized Topic getOrCreate(String topic) {
        Topic t = topics.get(topic);
        if(t == null) {
            t = new Topic(topic);
            topics.put(topic, t);
        }
        return t;
    }

    // Idee: Virtuelle Subscription hinterlegen mit ID, alle Events für die
    // ID buffern, neue HTTP-Route kann mit ID dann Buffer abfragen
    // http://localhost:8080/broker/subscribe/test-topic
    @GetMapping("/subscribe/{topic}")
    public String subscribe(@PathVariable String topic) {
        Topic t = getOrCreate(topic);
        UUID id = UUID.randomUUID();

        List<Event> list =new ArrayList<>();
        this.events.put(id, list);

        broker.subscribe(t, (event) -> {
            list.add(event);
        });

        return id.toString();
    }

    // http://localhost:8080/broker/publish/test-topic?message=HelloWorld
    @GetMapping("/publish/{topic}")
    public void publish(@PathVariable String topic, @RequestParam String message) {
        Topic t = getOrCreate(topic);
        this.broker.publish(t, new MessageEvent(message));
    }

    @GetMapping("/poll/{id}")
    public List<Event> poll(@PathVariable String id) {
        List<Event> list = this.events.get(UUID.fromString(id));
        if(list != null) {
            ArrayList<Event> bufferedEvents = new ArrayList<>(list);
            list.clear();
            return bufferedEvents;
        }
        return new ArrayList<>();
    }


    // Was wäre nötig, denkbar den Broker auf HTTP-basis
    // umzusetzen
    // -> HTTP ist Zustandslos, man würde mit jeder anfrage
    //    genau ein paket als antwort senden
    // -> Anfrage vom Client zum Server, antwort vom Server an Client

    // Warum ist ein Pub/Sub daher schwierig?
    // -> man brauch mehrere Pakete
    // -> subscription vom Client, aber dann müsste
    //    der Server den Client mit Events informieren

    // Wir müssen "Polling" betreiben, regelmäßig anfragen
    // und die Subscription sollte eine Art ID liefern, für
    // die man anfragt
}
