package com.example.logic.controller;
import com.example.logic.entity.User;
import com.example.logic.repository.UserRepository;
import com.example.logic.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;


    @PutMapping("/{id}/deactivate")
    public User deactivateUser(@PathVariable Long id) {
        return userDetailsService.deactivateUser(id);
    }
}


