package de.thi.informatik.edi.flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

interface Example2 {
    int run();
}
interface Example3 {
    int run(int a, int b);
}
class Test implements Example3 {
    public int run(int x, int y) {
        return x + y;
    }
}
public class LambdaExample {
    // Runnable ist ein Interface, FunctionalInterface
    // -> void run();
    public static void doSomething1(Runnable r) {
        r.run();
    }
    public static void doSomething2(Example2 ex) {
        int i = ex.run();
    }
    public static void doSomething3(Example3 ex) {
        int res = ex.run(21, 42);
    }

    public static void helloWorld() {
        System.out.println("Hello World!");
    }

    public void moreHello() {
        System.out.println("Hello World!");
    }

    public static int calc(int x, int y) {
        return x + y;
    }



    public static void main(String[] args) {
        doSomething1(() -> System.out.println("Hello World!"));
        doSomething2(() -> 21 * 1);
        doSomething3((x, y) -> x + y);
        doSomething3((x, y) -> {
            return x + y;
        });
        // ohne Lambda?
        doSomething3(new Test());
        doSomething3(new Example3() {
            public int run(int x, int y) {
                return x + y;
            }
        });

        LambdaExample example = new LambdaExample();
        example.moreHello();

        // Eta-Konvertierung
        // vor dem :: steht der Objekt oder Klassenbezug, an dem die
        // Methode aufgerufen wird
        doSomething1(() -> helloWorld());
        doSomething1(LambdaExample::helloWorld);

        doSomething1(() -> example.moreHello());
        doSomething1(example::moreHello);

        doSomething3((x, y) -> calc(x, y));
        doSomething3(LambdaExample::calc);

    }
}
