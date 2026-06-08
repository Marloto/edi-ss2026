package de.thi.informatik.edi.stream;

import java.time.Duration;
import java.util.UUID;

import de.thi.informatik.edi.stream.messages.PaymentMessage;
import de.thi.informatik.edi.stream.messages.ShoppingOrderMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoppingTopology {
  private static final Logger logger = LoggerFactory.getLogger(ShoppingTopology.class);

  public static Topology build() {
    StreamsBuilder builder = new StreamsBuilder();




    return builder.build();
  }
}
