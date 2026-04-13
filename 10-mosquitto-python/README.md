# Demo for MQTT with Python

## Prerequisites

- Docker and Docker Compose for running the Mosquitto broker
- Python 3.x

## Setup

Start Mosquitto-Service with Docker: `docker-compose up` or `docker-compose up -d` as background service.

Install venv and dependencies with:

```
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

If you already prepared venv, just use:

```
source venv/bin/activate
```

## Running the demo

... finally start script:

```
python pub.py
python sub.py
```

## Configuration

You can configure the publisher using environment variables:

- MQTT_HOST: MQTT broker address (default: localhost)
- FREQUENCY: Publishing frequency in milliseconds (default: 1000)

Example:

```
FREQUENCY=500 MQTT_HOST=broker.example.com python pub.py
```