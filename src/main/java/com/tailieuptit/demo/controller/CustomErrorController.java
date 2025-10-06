// src/main/java/com/tailieuptit/demo/controller/CustomErrorController.java
package com.tailieuptit.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == 404) {
                model.addAttribute("error", "Trang không tồn tại");
                model.addAttribute("message", "Trang bạn tìm kiếm không tồn tại hoặc đã bị xóa.");
                return "error/404";
            } else if (statusCode == 500) {
                model.addAttribute("error", "Lỗi server");
                model.addAttribute("message", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
                return "error/500";
            }
        }

        model.addAttribute("error", "Có lỗi xảy ra");
        model.addAttribute("message", "Đã xảy ra lỗi không mong muốn.");
        return "index"; // Fallback to home page if error templates don't exist
    }
}