import paho.mqtt.client as mqtt
import os

def on_connect(client, userdata, flags, rc):
    client.subscribe("test/rnd")
    if rc != 0:
        print("Error while subscribing to test")

def on_message(client, userdata, msg):
    print(msg.topic, msg.payload.decode())

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.connect(os.environ.get('MQTT_HOST', 'localhost'), 1883)

try:
    client.loop_forever()
except KeyboardInterrupt:
    print("Beende Programm...")
    client.loop_stop()
    client.disconnect()