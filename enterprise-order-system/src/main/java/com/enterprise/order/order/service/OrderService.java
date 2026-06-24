package com.enterprise.order.order.service;

import com.enterprise.order.order.model.Order;
import com.enterprise.order.order.repository.OrderRepository;
import com.enterprise.order.user.service.UserService;
import com.enterprise.order.inventory.service.InventoryService;
import com.enterprise.order.inventory.service.InsufficientStockException;
import com.enterprise.order.payment.service.PaymentService;
import com.enterprise.order.payment.service.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PaymentService paymentService;

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        log.info("Received request to create order with idempotency key: {}", order.getIdempotencyKey());

        // 1. Idempotency Check
        if (order.getIdempotencyKey() != null && !order.getIdempotencyKey().trim().isEmpty()) {
            Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(order.getIdempotencyKey());
            if (existingOrder.isPresent()) {
                log.info("Duplicate order request detected. Returning existing order ID: {}", existingOrder.get().getId());
                return existingOrder.get();
            }
        }

        // 2. Validate User (throws ResponseStatusException if user not found)
        userService.validateUser(order.getUserId());

        // 3. Save Order in PENDING status
        order.setStatus("PENDING");
        order.setStatusReason("Awaiting inventory reservation");
        order = orderRepository.save(order);
        log.info("Saved order ID: {} in PENDING state", order.getId());

        try {
            // 4. Reserve Inventory
            log.info("Attempting to reserve inventory for product ID: {}, quantity: {}", order.getProductId(), order.getQuantity());
            inventoryService.reserveStock(order.getProductId(), order.getQuantity());
            log.info("Stock reserved successfully for order ID: {}", order.getId());

            try {
                // 5. Process Payment
                log.info("Attempting to process payment for order ID: {}, amount: {}", order.getId(), order.getTotal());
                paymentService.processPayment(order.getId(), order.getTotal());
                
                // Success: Update Order to CONFIRMED
                order.setStatus("CONFIRMED");
                order.setStatusReason("Order completed successfully");
                order = orderRepository.save(order);
                log.info("Order ID: {} updated to CONFIRMED", order.getId());

            } catch (PaymentFailedException e) {
                // Compensating action: Release Stock
                log.warn("Payment failed for order ID: {}. Releasing stock.", order.getId());
                inventoryService.releaseStock(order.getProductId(), order.getQuantity());

                order.setStatus("FAILED");
                order.setStatusReason("Payment failed: " + e.getMessage());
                order = orderRepository.save(order);
                log.info("Order ID: {} updated to FAILED (payment failed)", order.getId());
            }

        } catch (InsufficientStockException e) {
            log.warn("Inventory reservation failed for order ID: {} - {}", order.getId(), e.getMessage());
            order.setStatus("FAILED");
            order.setStatusReason("Inventory reservation failed: " + e.getMessage());
            order = orderRepository.save(order);
            log.info("Order ID: {} updated to FAILED (out of stock)", order.getId());
        } catch (Exception e) {
            log.error("Unexpected error placing order ID: {}", order.getId(), e);
            order.setStatus("FAILED");
            order.setStatusReason("System error: " + e.getMessage());
            order = orderRepository.save(order);
        }

        return order;
    }
}
