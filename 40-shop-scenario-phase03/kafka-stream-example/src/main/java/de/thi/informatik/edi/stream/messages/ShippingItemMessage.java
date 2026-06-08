package de.thi.informatik.edi.stream.messages;

import java.util.UUID;

public class ShippingItemMessage {
    private UUID article;
    private int count;

    public ShippingItemMessage() {
    }

    public ShippingItemMessage(UUID article, int count) {
		super();
		this.article = article;
		this.count = count;
	}

	public UUID getArticle() {
        return article;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return "ShippingItemMessage [article=" + article + ", count=" + count + "]";
    }
}
