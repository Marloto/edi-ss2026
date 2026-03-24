package de.thi.informatik.edi.pubsub;

public class Topic {
    private String name;

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Topic topic) {
        return this.name.equals(topic.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
