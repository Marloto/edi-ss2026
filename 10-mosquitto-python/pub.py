import paho.mqtt.client as mqtt
import time
import random
import os

connected = False
def on_connect(client, userdata, flags, rc):
    global connected
    print(f"Connected with result code {rc}")
    connected = True

client = mqtt.Client()
client.on_connect = on_connect
client.connect(os.environ.get('MQTT_HOST', 'localhost'), 1883)
client.loop_start()

try:
    frequency = int(os.environ.get('FREQUENCY', 1000)) / 1000
    while True:
        if connected:
            v = random.random()
            client.publish("test/rnd", f"{v}")
            print(f"Send {v} to test/rnd {connected}")
        time.sleep(frequency)
except KeyboardInterrupt:
    print("Beende Programm...")
    client.loop_stop()
    client.disconnect()

