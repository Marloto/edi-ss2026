import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

public class Example {
    public static void main(String[] args) throws IOException {
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
                .publishOn(Schedulers.parallel());
        flux.subscribe(el -> {
            System.out.println("a: " + el + " (start)");
            try { Thread.sleep(1000); } catch(Exception e) { e.printStackTrace(); }
            System.out.println("a: " + el + " (stop)");
        });
        flux.subscribe(el -> System.out.println("b: " + el));
        System.in.read();
    }
}
