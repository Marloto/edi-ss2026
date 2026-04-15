package de.thi.informatik.edi.shop.shopping.services.messages;

import de.thi.informatik.edi.shop.shopping.model.Cart;
import de.thi.informatik.edi.shop.shopping.model.CartEntry;

import java.util.UUID;


public class ArticleAddedToCartMessage {
    private UUID id;
    private UUID article;
    private String name;
    private double price;
    private int count;

    public ArticleAddedToCartMessage() {

    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getArticle() {
        return article;
    }

    public void setArticle(UUID article) {
        this.article = article;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static ArticleAddedToCartMessage fromCartEntry(CartEntry entry, Cart cart) {
        ArticleAddedToCartMessage message = new ArticleAddedToCartMessage();
        message.article = entry.getId();
        message.id = cart.getId();
        message.name = entry.getName();
        message.price = entry.getPrice();
        message.count = entry.getCount();
        return message;
    }
}
