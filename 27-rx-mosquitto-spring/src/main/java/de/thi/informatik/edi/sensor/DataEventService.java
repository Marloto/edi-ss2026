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

	public DataEventService(MessageBrokerService service) {
		this.service = service;
	}

	private static final Pattern p = Pattern.compile("servers/(.*?)/(.*)");

	@PostConstruct
	public void init() {
	}

}