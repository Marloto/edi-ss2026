package de.thi.informatik.edi.sensor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class DataEventService {
	
	@Value("${mqtt.topic:servers/#}")
	private String topic;

	private MessageBrokerService service;
    private Flux<DataEvent> dataEvents;

    public DataEventService(MessageBrokerService service) {
		this.service = service;
	}

	private static final Pattern p = Pattern.compile("servers/(.*?)/(.*)");

    /**
     * servers/2913aff64661/cpu-usage 0.3915492957746479
     * servers/2913aff64661/cpu-free 0.6095505617977528
     * servers/2913aff64661/free-mem 4388.453125
     * servers/2913aff64661/process/usage-mem 77447168
     * servers/2913aff64661/process/heap-total 18575360
     * servers/2913aff64661/total-mem 7838.1328125
     * servers/2913aff64661/freemem-percentage 0.5598850172583753
     */

	@PostConstruct
	public void init() {
        // Topic, Wert als String
        Flux<Tuple2<String, String>> events = service.subscribeToTopic(topic);
        // ID für Server, Messwert Bezeichner und Wert herauslösen...
        Flux<DataEvent> dataEvents = events.map(tuple -> {
            String[] parts = tuple.getT1().split("/");
            String type = "";
            String server = "";
            double value = 0;
            if (parts.length >= 3) {
                server = parts[1];
                type = parts[2];
                if (parts.length >= 4) {
                    type += "/" + parts[3];
                }
                value = Double.parseDouble(tuple.getT2());
            }
            return new DataEvent(server, type, value);
        }).filter(data -> !"".equals(data.getServer()));
        this.dataEvents = dataEvents;
    }

    public Flux<DataEvent> getDataEvents() {
        return dataEvents;
    }
}