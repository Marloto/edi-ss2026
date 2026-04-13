import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publisher {
    public static void main(String[] args) {
        String topic = "sensoren/test/test";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "ExampleClient";
        MemoryPersistence persistence = new MemoryPersistence();

        try {

            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            connOpts.setWill("java/test", "bin offline".getBytes(), 1, true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            MqttMessage onlineMsg = new MqttMessage("bin online".getBytes());
            onlineMsg.setRetained(true);
            mqttClient.publish("java/test", onlineMsg);

            int i = 0;
            while (i < 100) {
                String content = "Test: " + System.currentTimeMillis();

                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);

                mqttClient.publish(topic, message);
                System.out.println("Published: " + content);
                i ++;

                Thread.sleep(1000);
            }

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}