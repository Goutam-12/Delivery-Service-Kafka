package com.amazon.delivery.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.amazon.delivery.event.PaymentSuccessEvent;
import com.amazon.delivery.service.DeliveryServices;

@Service
public class DeliveryKafkaConsumer {


	    private final DeliveryServices deliveryService;

	    public DeliveryKafkaConsumer(DeliveryServices deliveryService) {

	        this.deliveryService = deliveryService;
	    }


	    @KafkaListener(
	            topics = "payment-success",
	            groupId = "delivery-service-group",
	            containerFactory = "kafkaListenerContainerFactory"
	    )
	    public void consumePaymentSuccess(PaymentSuccessEvent paymentEvent) {

	        System.out.println("DeliveryKafkaConsumer received event");

	        System.out.println("EventId = "+ paymentEvent.getEventId());

	        System.out.println("OrderId = "+ paymentEvent.getOrderId());

	        System.out.println("CustomerId = " + paymentEvent.getCustomerId());

	        System.out.println("Payment Status = " + paymentEvent.getPaymentStatus());

	        deliveryService.createDelivery(paymentEvent);
	    }
}

