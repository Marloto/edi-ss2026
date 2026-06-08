package de.thi.informatik.edi.stream.messages;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShippingMessage {

    private String status;
    private UUID shippingIdentifier;
    private UUID orderRef;
    private List<ShippingItemMessage> items;

    public ShippingMessage() {
    }
    
    public ShippingMessage(String status, UUID shippingIdentifier, UUID orderRef, ShippingItemMessage...items) {
		this.status = status;
		this.shippingIdentifier = shippingIdentifier;
		this.orderRef = orderRef;
		this.items = Arrays.asList(items);
	}

	public String getStatus() {
        return status;
    }

    public UUID getShippingIdentifier() {
        return shippingIdentifier;
    }

    public UUID getOrderRef() {
        return orderRef;
    }

    public List<ShippingItemMessage> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ShippingMessage [status=" + status + ", shippingIdentifier=" + shippingIdentifier + ", orderRef="
                + orderRef + ", items=" + items + "]";
    }

}
