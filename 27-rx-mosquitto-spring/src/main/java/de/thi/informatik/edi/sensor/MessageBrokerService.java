package de.thi.informatik.edi.sensor;

import jakarta.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Service
public class MessageBrokerService {
    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client}")
    private String clientId;
    
	private MqttClient client;


	@PostConstruct
	private void setUp() {
		try {
            MemoryPersistence persistence = new MemoryPersistence();
            this.client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
	}

    public Flux<Tuple2<String, String>> subscribeToTopic(String topic) {
        Sinks.Many<Tuple2<String, String>> sink = Sinks.many().multicast().onBackpressureBuffer();
        try {
            client.subscribe("servers/#", (t, msg) -> {
                sink.tryEmitNext(Tuples.of(t, msg.toString()));
            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        return sink.asFlux();
    }

}
