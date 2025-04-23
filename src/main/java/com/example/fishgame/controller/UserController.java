package com.example.fishgame.controller;

import com.example.fishgame.model.User;
import com.example.fishgame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/is-exist")
    public Map<String, Object> checkUserExist(@RequestParam String userId) {
        boolean exists = userService.userExists(userId);
        return Map.of("code", exists ? 200 : 404, "msg", exists ? "User exists" : "User not found");
    }

    @PostMapping("/create")
    public Map<String, Object> createUser(@RequestBody User user) {
        if (userService.userExists(user.getUserId())) {
            return Map.of("code", 409, "msg", "User already exists");
        }
        userService.createUser(user);
        return Map.of("code", 201, "msg", "User created successfully");
    }

    @GetMapping("/basic")
    public Map<String, Object> getUserBasic(@RequestParam String userId) {
        return userService.getUserBasicInfo(userId);
    }

    @GetMapping("/generate-token")
    public Map<String, Object> generateToken(@RequestParam String secret) {
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp);
        String token = userService.generateToken(timestampStr, secret);
        return Map.of("code", 200, "msg", "Token generated successfully", "token", token, "timestamp", timestampStr);
    }

}
