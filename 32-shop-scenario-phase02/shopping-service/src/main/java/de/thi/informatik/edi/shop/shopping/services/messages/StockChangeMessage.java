package de.thi.informatik.edi.shop.shopping.services.messages;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StockChangeMessage {
	private double value;

	public StockChangeMessage() {
	}

	@JsonCreator
	public StockChangeMessage(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
