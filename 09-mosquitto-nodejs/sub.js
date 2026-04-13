const client = require('mqtt').connect(process.env.MQTT_HOST || 'mqtt://localhost:1883');

client.on('connect', () => {
    console.log("Connected to MQTT broker");
    client.subscribe("sensoren/+/temp", (err) => {
        if(err) console.err(err);
    });
})

client.on('message', (topic, message) => {
    console.log(topic, message.toString());
});