package de.thi.informatik.edi.flux;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.HashMap;

public class StorageExampleMqtt {
    public static void main(String[] args) {
        String topic = "test/rnd";
        String broker = "tcp://localhost:1883";
        String clientId = "ExampleServer";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            // Würde eine Cold-Sink erstellen und einen Flux bereitstellen
            // wann wird die Senke aktiv? Direkt oder beim ersten Subscribe auf dem Flux (nicht MQTT)
            // -> ungünstig, da jede subscription die senke neu erzeugt
//            Flux<String> products = Flux.<String>create(sink -> {
//                sampleClient.subscribe("products", (t, msg) -> {
//                    sink.next(msg.toString());
//                });
//            });

            Sinks.Many<String> productsSink = Sinks.many().multicast().onBackpressureBuffer();
            sampleClient.subscribe("products", (t, msg) -> {
                productsSink.tryEmitNext(msg.toString());
            });

            Sinks.Many<String> salesSink = Sinks.many().multicast().onBackpressureBuffer();
            sampleClient.subscribe("sales", (t, msg) -> {
                salesSink.tryEmitNext(msg.toString());
            });


            Flux<String> products = productsSink.asFlux(); // "<id>,<name>" alt. {"id": "1", "name": "Apfel"}
            Flux<String> sales = salesSink.asFlux();    // "<id>,<count>"

            // Wie die "csv" ähnlichen formate entpacken?
            Flux<Tuple2<String, String>> productMasterData = products.map(element -> {
               String[] arr = element.split(",");
               return Tuples.of(arr[0], arr[1]); // id, name
            }); // "<id>,<name>" -> Tuple2<String, String>

            Flux<Tuple2<String, String>> salesStream = sales.map(element -> {
                String[] arr = element.split(",");
                return Tuples.of(arr[0], arr[1]); // id, name
            }); // "<id>,<count>" -> Tuple2<String, String>

            Flux<HashMap<String, String>> productMap = productMasterData
                    .scan(new HashMap<String, String>(), (old, cur) -> {
                        old.put(cur.getT1(), cur.getT2());
                        return old;
                    });
            productMap.subscribe(System.out::println);
            Flux.combineLatest(productMap, salesStream,
                            (prods, sal) ->
                                    // sales.getT1 -> ID, sales.getT2 -> count
                                    Tuples.of(sal.getT1(), sal.getT2(),
                                            prods.getOrDefault(sal.getT1(), "Unknown")))
                    .map(el -> el.getT3() + " " +
                            el.getT2() + "x verkauft")
                    .subscribe(el -> {
                        MqttMessage msg = new MqttMessage();
                        msg.setPayload(el.getBytes());
                        try {
                            sampleClient.publish("results", msg);
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    });

            System.in.read();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            // ...
        } catch (IOException e) {
            // ...
        }
    }
}
