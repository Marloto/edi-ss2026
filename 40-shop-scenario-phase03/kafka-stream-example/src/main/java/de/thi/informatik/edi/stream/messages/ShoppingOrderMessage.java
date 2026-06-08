package de.thi.informatik.edi.stream.messages;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShoppingOrderMessage {

    private UUID id;
    private String firstName;
    private String lastName;
    private String street;
    private String zipCode;
    private String city;
    private String status;
    private double price;
    private List<ShoppingOrderItemMessage> items;
    
    public ShoppingOrderMessage() {
	}

    public ShoppingOrderMessage(UUID id, String firstName, String lastName, String street, String zipCode, String city, String status, double price, ShoppingOrderItemMessage...items) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
		this.status = status;
		this.price = price;
		this.items = Arrays.asList(items);
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public List<ShoppingOrderItemMessage> getItems() {
        return items;
    }

    public String toString() {
        return "ShoppingOrderMessage [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", street="
                + street + ", zipCode=" + zipCode + ", city=" + city + ", status=" + status + ", price=" + price
                + ", items=" + items + "]";
    }

}
