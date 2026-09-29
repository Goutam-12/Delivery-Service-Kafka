package com.amazon.delivery.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazon.delivery.entity.DeliveryEntity;
import com.amazon.delivery.event.DeliveryCreatedEvent;
import com.amazon.delivery.event.PaymentSuccessEvent;
import com.amazon.delivery.kafka.DeliveryKafkaProducer;
import com.amazon.delivery.repository.DeliveryRepository;

@Service
public class DeliveryServices {

	    @Autowired
	    DeliveryRepository deliveryRepository;

	    @Autowired
	    DeliveryKafkaProducer deliveryKafkaProducer;


	    public void createDelivery( PaymentSuccessEvent paymentEvent) {


	        // IDEMPOTENCY CHECK
	        
	        boolean alreadyProcessed = deliveryRepository.existsByPaymentEventId(paymentEvent.getEventId());

	        if (alreadyProcessed) {

	            System.out.println("Duplicate PAYMENT_SUCCESS detected");

	            System.out.println("Payment EventId = "+ paymentEvent.getEventId());

	            System.out.println("Skipping delivery creation");

	            return;
	        }

	        // VALIDATION
	        
	        if (paymentEvent.getOrderId() == null) {

	            throw new RuntimeException("OrderId cannot be null");
	        }

	        if (paymentEvent.getCustomerId() == null) {

	            throw new RuntimeException("CustomerId cannot be null");
	        }

	        if (paymentEvent.getPaymentStatus() == null) {

	            throw new RuntimeException("PaymentStatus cannot be null");
	        }


	        /*
	         * Delivery should only be created
	         * for successful payment.
	         */
	        if (!"SUCCESS".equals(paymentEvent.getPaymentStatus())) {

	            throw new RuntimeException("Delivery cannot be created "+ "for unsuccessful payment");
	        }

	        // GENERATE TRACKING NUMBER

	        String trackingNumber ="TRK-" + UUID.randomUUID();

	        // CREATE DELIVERY ENTITY

	        DeliveryEntity delivery =new DeliveryEntity();

	        delivery.setTrackingNumber(trackingNumber);

	        delivery.setOrderId(paymentEvent.getOrderId());

	        delivery.setCustomerId(paymentEvent.getCustomerId());

	        /*
	         * PaymentSuccessEvent currently does not contain
	         * deliveryAddress.
	         *
	         * Therefore this is temporarily set to
	         * "UNKNOWN".
	         *
	         * Better solution:
	         * include deliveryAddress in PaymentSuccessEvent.
	         */
	        delivery.setDeliveryAddress("UNKNOWN");

	        delivery.setDeliveryStatus("CREATED");

	        delivery.setPaymentEventId(paymentEvent.getEventId());


	        // SAVE DELIVERY

	        deliveryRepository.save(delivery);

	        System.out.println("Delivery created successfully");

	        System.out.println("Tracking Number = "+ trackingNumber);


	        // CREATE KAFKA EVENT

	        DeliveryCreatedEvent deliveryEvent =new DeliveryCreatedEvent();

	        deliveryEvent.setEventId("DEL-" + UUID.randomUUID());

	        deliveryEvent.setEventType("DELIVERY_CREATED");

	        deliveryEvent.setOrderId(paymentEvent.getOrderId());

	        deliveryEvent.setCustomerId(paymentEvent.getCustomerId());

	        deliveryEvent.setTrackingNumber(trackingNumber);

	        deliveryEvent.setDeliveryAddress(delivery.getDeliveryAddress());

	        deliveryEvent.setDeliveryStatus("CREATED");


	        // PUBLISH EVENT

	        deliveryKafkaProducer.sendDeliveryCreated(deliveryEvent);

	  
	   }
}


