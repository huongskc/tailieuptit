// src/main/java/com/tailieuptit/demo/controller/AuthController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // Store user in session
                session.setAttribute("currentUser", user);
                session.setAttribute("isLoggedIn", true);

                redirectAttributes.addFlashAttribute("message",
                        "Đăng nhập thành công! Chào mừng " + username);
                return "redirect:/";
            } else {
                // Service trả về null nghĩa là username hoặc password không đúng
                redirectAttributes.addFlashAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
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

            userService.registerUser(username, email, password);

            redirectAttributes.addFlashAttribute("message",
                    "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/auth/login";

        } catch (RuntimeException e) { // Bắt lỗi do Service ném ra (VD: Trùng user)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
}