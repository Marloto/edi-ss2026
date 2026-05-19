package de.thi.informatik.edi.streams;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.kstream.KStream;

public class SerdesExample {
	public static void main(String[] args) {
		StreamsBuilder builder = new StreamsBuilder();



		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev1");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// not required, uncomment
		//config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Void().getClass());
		//config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

	private static Serde<IdObject> mySerdes() {
		return Serdes.serdeFrom(
			(topic, obj) -> writeValue(obj),
			(topic, dataInBytes) -> readValue(dataInBytes)
		);
	}

	private static byte[] writeValue(IdObject data) {
		try {
			return new ObjectMapper().writeValueAsBytes(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	private static IdObject readValue(byte[] data) {
		try {
			return new ObjectMapper().readValue(data, IdObject.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new IdObject();
		}
	}
}
