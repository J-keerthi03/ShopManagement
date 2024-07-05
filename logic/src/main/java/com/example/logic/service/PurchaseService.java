package com.example.logic.service;

import com.example.logic.entity.Product;
import com.example.logic.repository.ProductRepository;
import com.example.logic.util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private ProductRepository productRepository;

    // Method to purchase a product by name and quantity
    @Transactional
    public void purchaseProduct(String productName, int quantity) {
        Optional<Product> optionalProduct = Optional.ofNullable(productRepository.findByName(productName));

        // Check if the product exists
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with name: " + productName);
        }

        Product product = optionalProduct.get();

        // Check if there is enough stock
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock available for product: " + productName);
        }

        // Update stock
        product.setStock(product.getStock() - quantity);

        // Save updated product
        productRepository.save(product);
    }
}
