package com.enterprise.order.inventory.controller;

import com.enterprise.order.inventory.model.Inventory;
import com.enterprise.order.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventory(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Inventory> addStock(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam int quantity) {
        Inventory updatedInventory = inventoryService.addStock(productId, productName, quantity);
        return ResponseEntity.ok(updatedInventory);
    }
}
