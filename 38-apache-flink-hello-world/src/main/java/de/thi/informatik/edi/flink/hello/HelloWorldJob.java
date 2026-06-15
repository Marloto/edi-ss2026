package de.thi.informatik.edi.flink.hello;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class HelloWorldJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // ...
        

        env.execute("Hello World");
    }
}
