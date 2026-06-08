package de.thi.informatik.edi.stream.messages;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ArticleAddedToCartMessage.class)
public class ArticleAddedToCartMessage extends CartMessage {
    private UUID id;
    private UUID article;
    private String name;
    private int count;
    private double price;

    public ArticleAddedToCartMessage() {
        super("added-to-cart");
    }
    
    public ArticleAddedToCartMessage(UUID article, String name, int count, double price) {
    	super("added-to-cart");
		this.id = UUID.randomUUID();
		this.article = article;
		this.name = name;
		this.count = count;
		this.price = price;
    }

    public UUID getArticle() {
        return article;
    }

    public UUID getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "ArticleAddedToCartMessage [id=" + id + ", article=" + article + ", name=" + name + ", count=" + count
                + ", price=" + price + "]";
    }
}
