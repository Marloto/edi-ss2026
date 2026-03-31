package de.thi.informatik.edi.pubsub.httpsse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

// curl -N http://localhost:8080/test-sse/test
@RestController()
@RequestMapping("/test-sse")
public class ExampleSse {

    // In Spring wird ServerSideEvent (SSE) mittels SSE Emitter realisiert
    @GetMapping("/test")
    public SseEmitter doSomething() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Thread t = new Thread(() -> {
            try {
                for(int i = 0; i < 10; i++) {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data("Test " + i + ": " + System.currentTimeMillis()));
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.setDaemon(true);
        t.start();

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to test"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
