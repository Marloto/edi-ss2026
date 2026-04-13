package de.thi.inf.sesa.mqtt.controller;

import de.thi.inf.sesa.mqtt.services.ConsumerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thi.inf.sesa.mqtt.services.PublisherService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/example")
public class ExampleController {
    private final ConsumerService consumerService;
    private PublisherService service;
	public ExampleController(PublisherService service, ConsumerService consumerService) {
		this.service = service;
        this.consumerService = consumerService;
	}

    @GetMapping("/list")
    public List<String> getMessages() {
        return this.consumerService.getMessages();
    }
	
    @GetMapping("/add/{msg}")
    public ResponseEntity doSomething(@PathVariable String msg) {
        service.publish("test", null, msg);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}