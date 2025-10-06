package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy tất cả user
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo id
    @GetMapping("/{username}")
    public Optional<User> getUserById(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }

    // Tạo user mới
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return "Deleted user with id " + id;
    }
}
