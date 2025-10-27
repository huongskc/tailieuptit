package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.UserService; // THAY ĐỔI: Dùng UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService; // THAY ĐỔI: Inject UserService

    // Lấy tất cả user
    @GetMapping
    public List<User> getAllUsers() {
        // THAY ĐỔI: Gọi Service
        return userService.getAllUsers();
    }

    // Lấy user theo id (Sửa lại: Nên dùng ID hoặc username thống nhất)
    // Giả sử vẫn dùng username như file gốc
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserById(@PathVariable String username) {
        // THAY ĐỔI: Gọi Service
        Optional<User> userOpt = userService.getUserByUsername(username);

        // Trả về 404 Not Found nếu không tìm thấy
        return userOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo user mới
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // THAY ĐỔI: Gọi Service
            User newUser = userService.createUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Xử lý lỗi (ví dụ: trùng username nếu logic được thêm vào service)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        try {
            // THAY ĐỔI: Gọi Service
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            // Lỗi do không tìm thấy user (ném ra từ Service)
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            // THAY ĐỔI: Gọi Service
            userService.deleteUser(id);
            return ResponseEntity.ok("Deleted user with id " + id);
        } catch (RuntimeException e) {
            // Lỗi do không tìm thấy user (ném ra từ Service)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}