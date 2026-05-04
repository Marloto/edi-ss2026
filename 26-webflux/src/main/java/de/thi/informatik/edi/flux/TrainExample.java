package de.thi.informatik.edi.flux;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

public class TrainExample {
    public static void main(String[] args) throws IOException {
        // 1. The Main Track: Trains arriving every 2 seconds
        Flux<String> arrivalTrack = Flux.just("Train-A", "Train-B")
                .delayElements(Duration.ofSeconds(1));

        // 2. The Unloading Process (The "Side Track")
        Flux<String> cargos = arrivalTrack.flatMap(train -> {
            return Flux.just("Cargo-1", "Cargo-2", "Cargo-3")
                    .delayElements(Duration.ofMillis(800))
                    .map(cargo -> "[" + train + "] delivered " + cargo);
        });

        cargos.subscribe(System.out::println);

        // Keep the main thread alive long enough to see the overlap
        System.in.read();
    }
}
