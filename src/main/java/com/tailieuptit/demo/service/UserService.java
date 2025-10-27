// src/main/java/com/tailieuptit/demo/service/UserService.java
package com.tailieuptit.demo.service;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ===== Logic xác thực & Session (Đã có) =====

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

    /**
     * Xác thực người dùng.
     * @return User nếu xác thực thành công, null nếu thất bại.
     */
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Trong thực tế, nên so sánh mật khẩu đã hash
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới.
     * @return User đã được tạo.
     * @throws RuntimeException nếu username hoặc email đã tồn tại.
     */
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

    // ===== Logic CRUD (Được chuyển từ Controller vào) =====

    /**
     * Lấy tất cả người dùng.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Lấy người dùng theo username.
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Lấy người dùng theo ID.
     */
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Tạo mới hoặc cập nhật user (dùng cho API).
     */
    public User createUser(User user) {
        // Có thể thêm logic kiểm tra trùng lặp ở đây nếu cần
        return userRepository.save(user);
    }

    /**
     * Cập nhật thông tin user.
     * @return User đã được cập nhật.
     * @throws RuntimeException nếu không tìm thấy user.
     */
    public User updateUser(Integer id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    /**
     * Xóa người dùng theo ID.
     * @throws RuntimeException nếu không tìm thấy user để xóa.
     */
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}