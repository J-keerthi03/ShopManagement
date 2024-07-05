package com.example.logic.controller;

import com.example.logic.entity.Product;
import com.example.logic.entity.User;
import com.example.logic.entity.UserProduct;
import com.example.logic.service.PurchaseService;
import com.example.logic.util.ResourceNotFoundException;
import com.example.logic.repository.ProductRepository;
import com.example.logic.repository.UserProductRepository;
import com.example.logic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-product")
public class UserProductController {

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @PostMapping
    public ResponseEntity<UserProduct> createUserProduct(@RequestBody UserProduct userProduct) {
        User user = userRepository.findById(userProduct.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(userProduct.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        userProduct.setUser(user);
        userProduct.setProduct(product);
        return ResponseEntity.ok(userProductRepository.save(userProduct));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<UserProduct>> getUserProducts(@PathVariable Long userId) {
        List<UserProduct> userProducts = userProductRepository.findByUserId(userId);
        return ResponseEntity.ok(userProducts);
    }
}
