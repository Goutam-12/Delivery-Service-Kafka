package com.amazon.delivery.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.amazon.delivery.event.DeliveryCreatedEvent;

@Service
public class DeliveryKafkaProducer {

	
	    @Autowired
	    private KafkaTemplate<String, Object> kafkaTemplate;


	    public void sendDeliveryCreated(DeliveryCreatedEvent event) {

	        System.out.println("Sending DELIVERY_CREATED to Kafka");

	        String key =String.valueOf(event.getOrderId());

	        kafkaTemplate.send(
	                "delivery-created",key,
	                event
	        ).whenComplete((result, exception) -> {

	            if (exception == null) {

	                System.out.println("DELIVERY_CREATED sent successfully");

	                System.out.println("OrderId = "+ event.getOrderId());

	                System.out.println("TrackingNumber = "+ event.getTrackingNumber());

	                System.out.println("Partition = "+ result.getRecordMetadata().partition());

	                System.out.println("Offset = "+ result.getRecordMetadata().offset());

	            } else {

	                System.err.println("DELIVERY_CREATED failed");

	                exception.printStackTrace();
	            }
	        });
	    }
}

