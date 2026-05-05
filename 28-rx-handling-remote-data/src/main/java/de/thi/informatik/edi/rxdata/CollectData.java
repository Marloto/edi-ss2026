package de.thi.informatik.edi.rxdata;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CollectData {
	
	@Value("${temp:http://localhost:3000/temperature}")
	private String tempEndpoint;
	@Value("${temp:http://localhost:3000/humidity}")
	private String humiEndpoint;
	
	private RestTemplateBuilder builder;

	public CollectData(@Autowired RestTemplateBuilder builder) {
		this.builder = builder;
	}
	
	/*
	 * Example for HTTP-Call: MeasurementValue[] values = 
	 *   builder.build().getForObject(url, MeasurementValue[].class); 
	 */
	private Mono<MeasurementValue[]> load(String url) {
		return Mono.create(sink ->
				sink.success(builder.build().getForObject(url, MeasurementValue[].class)));
	}
	
	public void find() {
	}
	
	
}
