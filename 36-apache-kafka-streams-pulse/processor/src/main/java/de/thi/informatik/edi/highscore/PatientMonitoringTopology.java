package de.thi.informatik.edi.highscore;

import java.security.Key;
import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thi.informatik.edi.highscore.model.BodyTemp;
import de.thi.informatik.edi.highscore.model.CombinedVitals;
import de.thi.informatik.edi.highscore.model.Pulse;

public class PatientMonitoringTopology {
	private static final Logger logger = LoggerFactory.getLogger(PatientMonitoringTopology.class);
	public static Topology build() {
		StreamsBuilder builder = new StreamsBuilder();



		return builder.build();
	}
}
