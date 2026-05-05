package de.thi.informatik.edi.sensor;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ExampleService {
	private DataEventService service;

	public ExampleService(@Autowired DataEventService service) {
		this.service = service;
	}

	@PostConstruct
	public void init() {
        Flux<DataEvent> dataEvents = this.service.getDataEvents();
        dataEvents
                .filter(data -> "cpu-usage".equals(data.getType()))
                .subscribe(System.out::println);
    }
}
