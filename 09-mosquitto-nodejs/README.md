# Demo for MQTT with Node.js

## Prerequisites
- Docker and Docker Compose for running the Mosquitto broker
- Node.js (v14 or higher)
- npm (comes with Node.js)

## Setup

Start Mosquitto-Service with Docker: 
```bash
docker-compose up
```

Or as background service:

```bash
docker-compose up -d
```

Install dependencies with:

```bash
npm i
```

## Running the demo

Start publisher and subscriber in separate terminals:

```bash
node pub.js
```

```bash
node sub.js
```

## Configuration

You can configure the publisher and subscriber using environment variables:
- `MQTT_HOST`: MQTT broker address (default: mqtt://localhost:1883)
- `FREQUENCY`: Publishing frequency in milliseconds (default: 1000)

Example:
```bash
FREQUENCY=500 MQTT_HOST=mqtt://broker.example.com node pub.js
```