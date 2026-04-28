package de.thi.informatik.edi.reactive;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ToStringSubscriber<T> implements Subscriber<T> {
    private Subscription subscription;

    public void onSubscribe(Subscription subscription) {
        System.out.println("Subscription added");
        subscription.request(1);
        this.subscription = subscription;
    }

    // Subscriber möchte Daten,
    // onNext beschreibt wie mit einem
    // neuen Datenpaket umgegangen wird
    public void onNext(T item) {
        System.out.println("Next " + item);
        subscription.request(1);
    }

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onComplete() {
        System.out.println("Subscription completed");
    }
}

// Ist eine Beispielimplementierung für eine Verarbeitung, die sowohl
// publiziert als auch konsumiert
class TransformProcessor<T, R> extends SubmissionPublisher<R> implements Processor<T, R>{
    private final Function<T, R> transformer;
    private Subscription subscription;

    private long batchSize = 32;
    private long limit = batchSize - (batchSize / 4);
    private long processedCount = 0;


    public TransformProcessor(Function<T, R> transformer) {
        this.transformer = transformer;
    }

    public void onSubscribe(Subscription subscription) {
        System.out.println("Processor subscription added");
        // Request von 1 geht als erster Start
        // ist in der Praxis aber unsinnig vom Overhead
        // besser wäre eine Menge von Daten anzufragen, die
        // man gut handhaben kann
        subscription.request(batchSize);
        this.subscription = subscription;
    }

    public void onNext(T item) {
        System.out.println("Processor item received: " + item);
        /*Integer value = Integer.valueOf(item.toString());
        this.submit((R) value);*/
        R r = transformer.apply(item);
        this.submit(r);
        this.processedCount ++;
        if(processedCount >= limit) {
            this.subscription.request(processedCount);
            this.processedCount = 0;
        }
    }

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onComplete() {
        System.out.println("Processor subscription completed");
        this.close();
    }
}

class FilterProcessor<T> extends SubmissionPublisher<T> implements Processor<T, T>{
    private final Predicate<T> filter;
    private Subscription subscription;
    public FilterProcessor(Predicate<T> filter) {
        this.filter = filter;
    }

    public void onSubscribe(Subscription subscription) {
        System.out.println("Filter subscription added");

        subscription.request(1);
        this.subscription = subscription;
    }

    public void onNext(T item) {
        System.out.println("Filter item received: " + item);
        if(this.filter.test(item)) {
            this.submit(item);
        }
        this.subscription.request(1);
    }

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onComplete() {
        System.out.println("Filter subscription completed");
        this.close();
    }
}

public class FlowExample {
	public static void main(String[] args) throws Exception {
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        TransformProcessor<String, Integer> strToIntMapper = new TransformProcessor<String, Integer>(
                Integer::valueOf
        );
        FilterProcessor<Integer> greaterThan10 = new FilterProcessor<Integer>(
                (value) -> value > 10
        );
        ToStringSubscriber toStringSub = new ToStringSubscriber<>();

        publisher.subscribe(strToIntMapper);
        strToIntMapper.subscribe(greaterThan10);
        greaterThan10.subscribe(toStringSub);


        List.of("1", "21", "2", "4", "3", "42")
                .forEach(publisher::submit);
        publisher.close();

        // Das was gerade entstanden ist, entspricht im einfachen
        // dem, was man mit typischen map- und filter-Operatoren
        // auf Datenstrukturen wie Listen / Maps realisieren würde.
        List.of("1", "21", "2", "4", "3", "42").stream()
                .map(Integer::valueOf)
                .filter((value) -> value > 10)
                .forEach(System.out::println);

        //new ToStringSubscriber<String>();
        //new ToStringSubscriber<Integer>();
        //new ToStringSubscriber<Employee>();

        do {
            Thread.sleep(1000);
        } while(publisher.estimateMaximumLag() > 0);
	}
}


class Employee {

    private double income;
    private String department;
    private int age;
    private String name;

    public Employee(String name, int age, String department, double income) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.income = income;
    }


    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}





