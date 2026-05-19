import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

public class Example {
    public static void main(String[] args) throws IOException {
//        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
//                .publishOn(Schedulers.parallel());
//        flux.subscribe(el -> {
//            System.out.println("a: " + el + " (start)");
//            try { Thread.sleep(1000); } catch(Exception e) { e.printStackTrace(); }
//            System.out.println("a: " + el + " (stop)");
//        });
//        flux.subscribe(el -> System.out.println("b: " + el));

        Hooks.onOperatorDebug();
        Flux.just("Orange", "Red", "Yellow")
                .log()
                .filter(el -> el != null)
                .map(el -> el.substring(0, 4))
                .subscribe(System.out::println);

        System.in.read();
    }
}
