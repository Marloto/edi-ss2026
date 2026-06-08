package de.thi.informatik.edi.highscore;

import java.security.Key;
import java.time.Duration;
import java.util.function.Consumer;

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

        KStream<String, Pulse> pulse = builder.stream(Configurator.PULSE_EVENTS,
                Consumed.with(Serdes.String(), JsonSerdes.pulse()).withTimestampExtractor(new VitalTimestampExtractor()));
        KStream<String, BodyTemp> bodyTemp = builder.stream(Configurator.BODY_TEMP_EVENTS,
                Consumed.with(Serdes.String(), JsonSerdes.bodyTemp()).withTimestampExtractor(new VitalTimestampExtractor()));

        TimeWindows window = TimeWindows.ofSizeAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(5));
        KStream<Windowed<String>, Long> pulseCount = pulse
                .groupByKey()
                .windowedBy(window)
                .count()
                .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
                .toStream();

        pulseCount.print(Printed.toSysOut());

        KStream<String, Long> highPulse = pulseCount
                .filter((key, value) -> value >= 90)
                .map((key, value) -> KeyValue.pair(key.key(), value));

        highPulse.print(Printed.toSysOut());

        KStream<String, BodyTemp> highTemp = bodyTemp
                .filter((key, value) -> value.getTemperature() >= 38);
        highTemp.print(Printed.toSysOut());

        // KStream zu KStream muss immer in einem Zeitfenster passieren
        JoinWindows joinWindows = JoinWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(60), Duration.ofSeconds(5));
        KStream<String, CombinedVitals> vitals = highPulse.join(highTemp,
                (pulseCountEvent, bodyEvent) -> new CombinedVitals(pulseCountEvent.intValue(), bodyEvent),
                joinWindows,
                StreamJoined.with(Serdes.String(), Serdes.Long(), JsonSerdes.bodyTemp()));


        vitals.print(Printed.toSysOut());
        vitals.to(Configurator.ALERTS, Produced.with(Serdes.String(), JsonSerdes.combinedVitals()));


        return builder.build();
	}
}
