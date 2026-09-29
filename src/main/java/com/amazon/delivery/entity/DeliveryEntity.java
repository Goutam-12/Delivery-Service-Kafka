package com.amazon.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="deliveries")
public class DeliveryEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String trackingNumber;

	    private Long orderId;

	    private Long customerId;

	    private String deliveryAddress;

	    private String deliveryStatus;

	    
	    private String paymentEventId;

	    public DeliveryEntity() {
	    }

	    public Long getId() {
	        return id;
	    }

	    public String getTrackingNumber() {
	        return trackingNumber;
	    }

	    public void setTrackingNumber(String trackingNumber) {
	        this.trackingNumber = trackingNumber;
	    }

	    public Long getOrderId() {
	        return orderId;
	    }

	    public void setOrderId(Long orderId) {
	        this.orderId = orderId;
	    }

	    public Long getCustomerId() {
	        return customerId;
	    }

	    public void setCustomerId(Long customerId) {
	        this.customerId = customerId;
	    }

	    public String getDeliveryAddress() {
	        return deliveryAddress;
	    }

	    public void setDeliveryAddress(String deliveryAddress) {
	        this.deliveryAddress = deliveryAddress;
	    }

	    public String getDeliveryStatus() {
	        return deliveryStatus;
	    }

	    public void setDeliveryStatus(String deliveryStatus) {
	        this.deliveryStatus = deliveryStatus;
	    }

	    public String getPaymentEventId() {
	        return paymentEventId;
	    }

	    public void setPaymentEventId(String paymentEventId) {
	        this.paymentEventId = paymentEventId;
	    }
}

