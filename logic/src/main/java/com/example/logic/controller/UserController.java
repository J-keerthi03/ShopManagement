package com.example.logic.controller;
import com.example.logic.entity.Product;
import com.example.logic.entity.PurchaseRequest;
import com.example.logic.entity.User;
import com.example.logic.repository.UserRepository;
import com.example.logic.service.PurchaseService;
import com.example.logic.util.ResourceNotFoundException;
import com.example.logic.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
    @RequestMapping("/user")
    public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseService purchaseService;


    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseProduct(@RequestBody PurchaseRequest request) {
        purchaseService.purchaseProduct(request.getProductName(), request.getQuantity());
        return ResponseEntity.ok().body("Product purchased successfully.");
    }


    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            return ResponseEntity.ok(product);
        }


    @GetMapping("/details")
    public ResponseEntity<User> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

}
