package de.thi.informatik.edi.shop.shopping.services.messages;

public class StockMessage {
    private double value;
    public StockMessage(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
