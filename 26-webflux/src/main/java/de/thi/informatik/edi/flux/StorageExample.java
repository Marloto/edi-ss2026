package de.thi.informatik.edi.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

//https://pastie.io/njcnco.java
public class StorageExample {
    public static void main(String[] args) throws IOException {
        // (ID, Name)
        Flux<Tuple2<String, String>> productMasterData = Flux.just(
                Tuples.of("1", "Apfel"),
                Tuples.of("2", "Banane"),
                Tuples.of("3", "Orange")
        ).delayElements(Duration.ofMillis(1000));

        // (ID, Menge)
        Flux<Tuple2<String, Integer>> salesStream = Flux.interval(Duration.ofMillis(1000))
                .map(i -> Tuples.of(
                        Long.valueOf(i % 3 + 1).toString(),
                        (int) (Math.random() * 5 + 1)));

        // Wie könnte man die statischen Produkt-Daten mit den Sales Stream kombinieren?
        // Tipp: reduce (bzw. scan), combineLatest
        // Apfel 3x Verkauft
        // Banane 2x Verkauft
        // ...

        Mono<Map<String, String>> reduce = productMasterData
                .map(el -> {
                    Map<String, String> map = new HashMap<>();
                    map.put(el.getT1(), el.getT2());
                    return map;
                }).reduce((old, cur) -> {
                    for (Map.Entry<String, String> entry : cur.entrySet()) {
                        old.put(entry.getKey(), entry.getValue());
                    }
                    return old;
                });

        Flux.combineLatest(reduce, salesStream,
                (products, sales) ->
                        // sales.getT1 -> ID, sales.getT2 -> count
                        Tuples.of(sales.getT1(), sales.getT2(),
                                products.containsKey(sales.getT1()) ? products.get(sales.getT1()) : "Unknown"))
                .subscribe(el -> System.out.println(el.getT3() + " " +
                        el.getT2() + "x verkauft"));


        System.in.read();
    }
}
