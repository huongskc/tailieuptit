// src/main/java/com/tailieuptit/demo/controller/AuthController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Trang đăng nhập
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Đăng nhập");
        return "login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (password.equals(user.getPassword())) {
                    // Store user in session
                    session.setAttribute("currentUser", user);
                    session.setAttribute("isLoggedIn", true);

                    redirectAttributes.addFlashAttribute("message",
                            "Đăng nhập thành công! Chào mừng " + username);
                    return "redirect:/";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu không đúng!");
                    return "redirect:/auth/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại!");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("currentUser");
        session.removeAttribute("isLoggedIn");
        session.invalidate();

        redirectAttributes.addFlashAttribute("message", "Đã đăng xuất thành công!");
        return "redirect:/";
    }

    // Trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Đăng ký");
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Validate input
            if (username.length() < 3) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập phải có ít nhất 3 ký tự!");
                return "redirect:/auth/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                return "redirect:/auth/register";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/auth/register";
            }

            // Check if username or email already exists
            if (userRepository.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "redirect:/auth/register";
            }

            if (userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                return "redirect:/auth/register";
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            userRepository.save(newUser);

            redirectAttributes.addFlashAttribute("message",
                    "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
}