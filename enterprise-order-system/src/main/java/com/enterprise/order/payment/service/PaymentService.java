package com.enterprise.order.payment.service;

import com.enterprise.order.payment.model.Payment;
import com.enterprise.order.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional
    public Payment processPayment(Long orderId, BigDecimal amount) {
        log.info("Processing payment for order ID: {}, amount: {}", orderId, amount);

        String status = "COMPLETED";
        if (amount.compareTo(new BigDecimal("1000.00")) > 0) {
            status = "FAILED";
        }

        Payment payment = new Payment(orderId, amount, status);
        payment = paymentRepository.save(payment);

        if ("FAILED".equals(status)) {
            log.warn("Payment failed for order ID: {} because amount exceeds 1000.00 (simulated failure)", orderId);
            throw new PaymentFailedException("Amount exceeds limit (simulation)");
        }

        log.info("Payment completed successfully for order ID: {}", orderId);
        return payment;
    }
}
