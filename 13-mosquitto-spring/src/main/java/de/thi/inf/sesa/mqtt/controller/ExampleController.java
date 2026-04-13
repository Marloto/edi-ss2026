package de.thi.inf.sesa.mqtt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thi.inf.sesa.mqtt.services.PublisherService;

@RestController
@RequestMapping("/example")
public class ExampleController {
	private PublisherService service;
	public ExampleController(PublisherService service) {
		this.service = service;
	}

    @GetMapping("/msg/{msg}")
    public ResponseEntity doSomething(@PathVariable String msg) {
        service.publish(msg);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}