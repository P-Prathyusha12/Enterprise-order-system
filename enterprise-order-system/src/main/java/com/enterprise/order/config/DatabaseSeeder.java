package com.enterprise.order.config;

import com.enterprise.order.user.model.User;
import com.enterprise.order.user.repository.UserRepository;
import com.enterprise.order.inventory.model.Inventory;
import com.enterprise.order.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database seeding...");

        // Seed Users
        if (userRepository.count() == 0 || userRepository.findByUsername("alice").isPresent()) {
            // Remove old seeds if they exist
            userRepository.findByUsername("alice").ifPresent(userRepository::delete);
            userRepository.findByUsername("bob").ifPresent(userRepository::delete);
            userRepository.findByUsername("charlie").ifPresent(userRepository::delete);
            
            // Seed new Indian names
            userRepository.save(new User(null, "aarav", "aarav@enterprise.com"));
            userRepository.save(new User(null, "vihaan", "vihaan@enterprise.com"));
            userRepository.save(new User(null, "diya", "diya@enterprise.com"));
            log.info("Users table seeded with Indian names.");
        } else {
            log.info("Users table already seeded. Skipping user seeding.");
        }

        // Seed Inventory
        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(new Inventory(101L, "Enterprise Laptop", 10));
            inventoryRepository.save(new Inventory(102L, "Wireless Mouse", 50));
            inventoryRepository.save(new Inventory(103L, "Mechanical Keyboard", 5));
            log.info("Inventory table seeded.");
        } else {
            log.info("Inventory table already has data. Skipping inventory seeding.");
        }

        log.info("Database seeding completed.");
    }
}
