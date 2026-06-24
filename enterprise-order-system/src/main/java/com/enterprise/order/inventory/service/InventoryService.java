package com.enterprise.order.inventory.service;

import com.enterprise.order.inventory.model.Inventory;
import com.enterprise.order.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Optional<Inventory> getInventory(Long productId) {
        return inventoryRepository.findById(productId);
    }

    @Transactional
    public Inventory addStock(Long productId, String productName, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElse(new Inventory(productId, productName, 0));
        inventory.setStock(inventory.getStock() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void reserveStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (inventory.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product ID " + productId + 
                    ". Available: " + inventory.getStock() + ", Requested: " + quantity);
        }

        inventory.setStock(inventory.getStock() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void releaseStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found during rollback"));

        inventory.setStock(inventory.getStock() + quantity);
        inventoryRepository.save(inventory);
    }
}
