package com.example.fishgame.service;

import com.example.fishgame.model.User;
import com.example.fishgame.repository.UserRepository;
import com.example.fishgame.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    public boolean userExists(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public Map<String, Object> getUserBasicInfo(String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return Map.of(
                "code", 200,
                "msg", "Success",
                "data", Map.of(
                    "userId", user.getUserId(),
                    "userName", user.getUserName(),
                    "level", user.getLevel(),
                    "coins", user.getCoins(),
                    "diamonds", user.getDiamonds()
                )
            );
        }
        return Map.of("code", 404, "msg", "User not found");
    }

    public String generateToken(String timestamp, String secret) {
        return tokenUtils.generateToken(secret, timestamp);
    }
}
