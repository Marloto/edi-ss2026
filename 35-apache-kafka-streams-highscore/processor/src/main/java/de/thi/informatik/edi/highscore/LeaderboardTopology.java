package de.thi.informatik.edi.highscore;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import de.thi.informatik.edi.highscore.model.Enriched;
import de.thi.informatik.edi.highscore.model.Leaderboard;
import de.thi.informatik.edi.highscore.model.Player;
import de.thi.informatik.edi.highscore.model.Product;
import de.thi.informatik.edi.highscore.model.ScoreEvent;
import de.thi.informatik.edi.highscore.model.ScoreWithPlayer;

public class LeaderboardTopology {
    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Player> players = builder.table("players",
                Consumed.with(Serdes.String(), JsonSerdes.player()));
        GlobalKTable<String, Product> products = builder.globalTable("products",
                Consumed.with(Serdes.String(), JsonSerdes.product()));
        KStream<String, ScoreEvent> scoreEvents = builder.stream("score-events",
                Consumed.with(Serdes.String(), JsonSerdes.scoreEvent()));



        // Consumern, Topics und Partitionen in Kafka?
        // -> wieviele consumer können ein topics lesen, mehrere möglich
        // -> consumer haben eine group-id
        // -> **wieviele consumer können eine partition lesen?**
        // -> wieviele consumer der selben group-id können eine partition lesen?
        //    -> max. einer
        // -> wie wirken group-id und partitonen bei consumern zusammen?
        // -> 4 consumer in einer gruppe mit id "foo" auf 3 partitionen -> 3
        //    consumer lesen je eine partition, einer macht nichts
        // -> 4 consumer in einer gruppe mit id "foo" auf 5 partitionen ->
        //    alle 4 consumer lesen je eine partition, einer eine zusätzlich

        // Warum kann das ein problem beim verarbeiten von daten sein?
        // -> Kafka Stream Prozesse sind ebenfalls Consumer... wenn wir z.B.
        //    ein Prozess (der selbst ein Consumer ist) möchte {"playerId":4,"productId":3,"score":999}
        //    und... {"id":4,"name":"NightWolf"} aus einem anderen topic
        //    sowie... {"id":3,"name":"Super Smash Bros"} aus dem nächsten
        // -> Probleme mit der Reihenfolge, Player / Produkt kommt später
        // -> Wie hat Kafka Events über die Partitionen verteilt?
        // -> Je Record in Kafka gab es Timestamp, Value und Key
        // -> bei bestimmten Keys landen wir in einer bestimmten Partition
        // -> z.B. Player verwendet als Key die ID, genauso wie Product, id 3 könnte in Partition 3
        //    landen, und id 4 in Partiton 4
        // WICHTIG: Jeder Kafka-Streams-Prozess erhält nur einen Teil der Events, damit kennt er z.B.
        // nie alle Player.

        // Ziel: join von scoreEvents mit Playern
        // -> um da hin zu kommen, müssen wir die Partitionen angleichen
        // -> wir müssen am Ende beim selben Prozess raus kommen

        KStream<String, ScoreEvent> scoreEventsWithKeyPlayerId = scoreEvents
                .selectKey((k, v) -> v.getPlayerId().toString());
        // Unterbrechung der Topologie, neuvergabe der ID sorgt für ein Produce gegen Kafka
        // virtuelles Topic erhält jetzt ScoreEvent zzgl. Key mit PlayerID

        // Finally, join
        KStream<String, ScoreWithPlayer> scoreWithPlayer = scoreEventsWithKeyPlayerId.join(players, ScoreWithPlayer::new,
                Joined.with(Serdes.String(), JsonSerdes.scoreEvent(), JsonSerdes.player()));

        // Letzter Schritt: ScoreWithPlayer mit Produkt kombinieren
        // -> Entscheidung: als key für das ergebnis eine ProduktID verwenden, hierfür Leaderboards
        KStream<String, Enriched> stream = scoreWithPlayer.join(products, (left, right) ->
                right.getScoreEvent().getProductId().toString(), Enriched::new);

        // in Stream haben wir jetzt Enriched Objekte (playerId, productId, ...Name und Score)
        // -> eine Liste umwandeln?
        // -> ordnen, und beschränken, alle Elemente zusammensammeln
        // -> reduce? berechnet altzustand + aktuellesEvent -> neuZustand
        // -> wie kann man daraus ein Leaderboard mit besten drei Elementen erstellen?
        // -> Trick war: "zustand" war eine geeignete Datenstruktur, z.B. Map
        // -> Welche Datenstruktur ist sortiert? und wo bekommt man schnell die top 3
        // -> Bäume

        KTable<String, Leaderboard> result = stream.groupBy((key, value) -> value.getProductId().toString(),
                        Grouped.with(Serdes.String(), JsonSerdes.enriched()))
                .aggregate(Leaderboard::new, (key, value, state) -> state.update(value),
                        Materialized.with(Serdes.String(), JsonSerdes.leaderboard()));

        result.toStream().to(Configurator.HIGH_SCORES);

        return builder.build();
    }
}
