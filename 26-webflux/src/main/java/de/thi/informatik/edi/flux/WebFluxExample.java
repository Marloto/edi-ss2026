package de.thi.informatik.edi.flux;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.util.function.Tuples;

public class WebFluxExample<T> {
	public static void main(String[] args) throws IOException {
        Mono<String> colorPublisher = Mono.just("RED"); // Publisher der ein Element bereitstellt
        Mono.empty(); // liefert einen leeren Publisher
        colorPublisher.subscribe(System.out::println);

        Flux<String> colorsPublisher = Flux.just("RED", "BLUE", "ORANGE");
        colorsPublisher.subscribe(System.out::println);

        Flux<String> example = Flux.just("RED", "BLUE", "ORANGE");
        Mono<List<String>> listMono = example
                .collectList();
        List<String> list = listMono.block();

        //Flux<Integer> exampleFromArray = Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4});
        //Flux<Long> intervaled = Flux.interval(Duration.ofSeconds(1));
        //intervaled.subscribe(System.out::println);

//        Flux<String> objectFlux = Flux.<String>create(sink -> {
//            sink.next("A");
//            sink.next("B");
//            sink.next("C");
//            // Kafka -> hier poll, und dann je Record ein next aufruf
//        });
//        objectFlux.subscribe(System.out::println);

//        Flux<String> flux = Flux.create(sink -> {
//            System.out.println("Created");
//            sink.next("A");
//        });
//        flux.subscribe(System.out::println);
//        flux.subscribe(System.out::println);

//        Many<Object> many = Sinks.many().multicast().onBackpressureBuffer();
//        Flux<Object> flux = many.asFlux();
//        flux.subscribe(System.out::println);
//        flux.subscribe(System.out::println);
//        // ... later ...
//        many.tryEmitNext(1.0);

//        Flux.interval(Duration.ofSeconds(1))
//                .filter(el -> el % 2 == 0)
//                .subscribe(System.out::println);

//        Flux.interval(Duration.ofSeconds(1))
//                .skip(2)
//                .subscribe(System.out::println);

        // Count-Operatoren haben die zusätzliche Anforderung, dass der Flux
        // terminierend sein muss, andernfalls wird der Mono nie ein Ereignis
        // emitieren
//        Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4})
//                .count()
//                .subscribe(System.out::println);

//        Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4})
//                .reduce((a, b) -> Math.max(a, b))
//                .subscribe(System.out::println);

        // Ungünstig, da concat mit terminierenden Publishern arbeitet
//        Flux.concat(
//                Flux.interval(Duration.ofSeconds(1)).map(el -> 'A'),
//                Flux.interval(Duration.ofSeconds(1)).map(el -> 'B')
//        ).subscribe(System.out::print);


        // Wichtig: merge arbeitet alle verfügbaren Elemente eines Publishers
        // ab, bevor er den nächsten anschaut, kann aber mit nicht terminierenden
        // Publishern umgehen
//        Flux.merge(
//                Flux.fromArray(new Integer[] {1, 2, 2, 1, 3, 2, 4}),
//                Flux.fromArray(new Integer[] {9, 8, 8, 9, 7, 8, 6})
//        ).subscribe(System.out::print);

//        Flux<Double> a = Flux.just(1.0, 5.0, 2.0, 3.0);
//        Flux<Double> b = Flux.just(7.0, 2.0, 4.0, 9.0);
//        Flux.zip(a, b).subscribe(System.out::println);
//
//        Flux<Long> a2 = Flux.interval(Duration.ofSeconds(1));
//        Flux<Long> b2 = Flux.interval(Duration.ofSeconds(2));
//        Flux.combineLatest(a2, b2, (x, y) -> List.of(x, y))
//                .subscribe(System.out::println);
//
//        // Flat-Map: 1:n Operator
//        Flux.just("World", "Foo", "Bar").flatMap(el -> {
//            List<Character> l = new ArrayList<>();
//            for(char c : el.toCharArray()) {
//                l.add(c);
//            }
//            return Flux.fromIterable(l);
//        });

//        Flux.interval(Duration.ofSeconds(1))
//                .buffer(3)
//                .subscribe(System.out::println);
//
//        Flux.interval(Duration.ofSeconds(1))
//                .window(3)
//                .subscribe(win -> win
//                        .reduce((a, b) -> a + b)
//                        .subscribe(System.out::println));
//
//        Flux.interval(Duration.ofSeconds(1))
//                .window(3)
//                .flatMap(win -> win.reduce((a, b) -> a + b))
//                .subscribe(System.out::println);


        Flux.just(5, 3, 42, 1, 10, 11)
            .reduce(Tuples.of(0,0), (old, cur) ->
                Tuples.of(old.getT1() + cur, old.getT2() + 1))
            .map(el -> el.getT1() / el.getT2())
            .subscribe(System.out::println);

        Flux.just(5, 3, 42, 1, 10, 11)
            .map(el -> Tuples.of(el, 1))
            .reduce((old, cur) ->
                Tuples.of(old.getT1() + cur.getT1(), old.getT2() + 1))
            .map(el -> el.getT1() / el.getT2())
            .subscribe(System.out::println);
//

//        Flux<Integer> just = Flux.just(5, 3, 42, 1, 10, 11);
//        just.map(el -> Tuples.of(el, 1))
//            .reduce((old, cur) -> {
//                return Tuples.of(old.getT1() + cur.getT1(), old.getT2() + 1);
//            })
//            .map(el -> el.getT1() / el.getT2())
//            .subscribe(System.out::println);
//
//        Flux.just(5, 3, 42, 1, 10, 11)
//                .window(3, 1)
//                .flatMap(part ->
//                    part
//                        .map(el -> Tuples.of(el, 1))
//                        .reduce((old, cur) -> {
//                            return Tuples.of(old.getT1() + cur.getT1(), old.getT2() + 1);
//                        })
//                        .map(el -> (double)el.getT1() / (double)el.getT2())
//                ).subscribe(System.out::println);

        // Prevent stopping of main thread
		System.in.read();
	}
}
