package de.thi.informatik.edi.stream.messages;

import java.util.UUID;

public class PaymentMessage {
    private UUID id;
    private UUID orderRef;
    private String status;
    private String statusBefore;

    public PaymentMessage() {
    }
    
    public PaymentMessage(UUID payment, UUID order, String status, String statusBefore) {
		id = payment;
		orderRef = order;
		this.status = status;
		this.statusBefore = statusBefore;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderRef() {
        return orderRef;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusBefore() {
        return statusBefore;
    }

    public String toString() {
        return "PaymentMessage [id=" + id + ", orderRef=" + orderRef + ", status=" + status + ", statusBefore="
                + statusBefore + "]";
    }
}
