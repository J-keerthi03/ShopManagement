package com.example.logic.service;

import com.example.logic.entity.User;
import com.example.logic.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Update last login time and set active status to true
        user.setLastLoginTime(LocalDateTime.now());
        user.setActive(true); // Assuming user is active upon login

        // Save updated user (optional)
        userRepository.save(user);

        return buildUserDetails(user);
    }

//    @Transactional
//    public User logout(String username) {
//        User user = userRepository.findByUsername(username);
//        if (user != null) {
//            user.setActive(false);
//            userRepository.save(user);
//            // Log the user status after update
//            System.out.println("User status after update: " + user.isActive());
//        }
//        return user;
//    }
    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(), // Use user's active status
                true, true, true,
                getAuthorities(user)
        );
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User deactivateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(false);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }
}
