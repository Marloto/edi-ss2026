package de.thi.informatik.edi.stream.messages;

import java.util.UUID;

public class ShoppingOrderItemMessage {

    private UUID article;
    private String name;
    private double price;
    private int count;

    public ShoppingOrderItemMessage() {
    }
    
    public ShoppingOrderItemMessage(UUID article, String name, int count, double price) {
		this.article = article;
		this.name = name;
		this.price = price;
		this.count = count;
    }

    public UUID getArticle() {
        return article;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return "ShoppingOrderItemMessage [article=" + article + ", name=" + name + ", price=" + price + ", count="
                + count + "]";
    }

}
