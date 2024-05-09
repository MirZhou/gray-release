package cn.eros.micro.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eros
 * @since 2024/5/9 15:16
 */
@RestController
public class HomeController {
    @Value("${server.name:}")
    private String serverName;

    @GetMapping("/")
    public String home() {
        return "Hello, Micro App! From: " + this.serverName;
    }
}
