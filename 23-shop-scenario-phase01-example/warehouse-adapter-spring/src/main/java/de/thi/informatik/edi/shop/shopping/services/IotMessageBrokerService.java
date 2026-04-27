package de.thi.informatik.edi.shop.shopping.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class IotMessageBrokerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${mqtt.broker:tcp://localhost:1883}")
    private String broker;

    @Value("${mqtt.client:warehouse-adapter}")
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

    private void send(String topic, byte[] message) {
        try {
            client.publish(topic, new MqttMessage(message));
        } catch (MqttException e) {
            e.printStackTrace();
            logger.error("Error while handling: " + new String(message), e);
        }
    }

    public void publish(String topic, String message) {
    	logger.info("Send (" + topic + "): " + message);
        send(topic, message.getBytes());
    }
    
    public interface IoTMessageBrokerServiceHandler {
		void handle(String t, String msg);
    }

    public void subscribe(String topic, IoTMessageBrokerServiceHandler handler) {
    	try {
	    	this.client.subscribe(topic, (t, msg) -> {
	    		logger.info("Received (" + t + "): " + msg);
	            handler.handle(t, new String(msg.getPayload()));
	        });
	    } catch (MqttException e) {
	    	e.printStackTrace();
	    }
    }
}
