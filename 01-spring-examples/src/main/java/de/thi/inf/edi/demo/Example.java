package de.thi.inf.edi.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Example {
    // Alte Variante, besser Konstruktor nehmen
    // @Autowired
    // private ExampleService exampleService;

    private ExampleService exampleService;
    private PersonRepository personRepo;

    public Example(ExampleService exampleService, PersonRepository personRepository) {
        this.exampleService = exampleService;
        this.personRepo = personRepository;

    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/hello2/{name}")
    public String hello(@PathVariable String name) {
        return "Hello " + name;
    }

    @PostMapping("/hello3")
    public String hello3(@RequestBody String name) { // <- wie plain text?
        return "Hello " + name;
    }

    @PostMapping("/hello4")
    public String hello4(@RequestBody Message name) {
        this.exampleService.doSomething();
        Person p = new Person(name.getMessage());
        this.personRepo.save(p);
        return "Hello " + name.getMessage();
    }

    @GetMapping("/persons")
    public List<Person> getPersons() {
        return this.personRepo.findAll();
    }
}
