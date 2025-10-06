// src/main/java/com/tailieuptit/demo/service/UserService.java
package com.tailieuptit.demo.service;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getCurrentUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Refresh user data from database
            return userRepository.findById(currentUser.getUserId());
        }
        return Optional.empty();
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    public boolean isAdmin(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && "admin".equals(currentUser.getUsername());
    }

    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public User registerUser(String username, String email, String password) {
        // Check if username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In production, hash this password

        return userRepository.save(newUser);
    }
}