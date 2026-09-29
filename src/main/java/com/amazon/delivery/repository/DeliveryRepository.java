package com.amazon.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amazon.delivery.entity.DeliveryEntity;

public interface DeliveryRepository
	        extends JpaRepository<DeliveryEntity, Long> {

	    boolean existsByPaymentEventId(String paymentEventId);

	    Optional<DeliveryEntity> findByPaymentEventId(String paymentEventId);

	    Optional<DeliveryEntity> findByOrderId(Long orderId);

	    Optional<DeliveryEntity> findByTrackingNumber(String trackingNumber);
}

