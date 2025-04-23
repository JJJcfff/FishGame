package com.example.fishgame.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenUtils {

    @Value("${security.token.secret}")
    private String secret;

    public String getSecret() {
        return this.secret;
    }

    public boolean validateToken(String token, String timestamp) {
        if (token == null || token.isEmpty() || timestamp == null || timestamp.isEmpty()) {
            return false;
        }

        String expectedToken = generateToken(this.secret, timestamp);
        return expectedToken != null && expectedToken.equals(token);
    }

    public String generateToken(String secret, String timestamp) {
        if (secret == null || secret.isEmpty() || !secret.equals(this.secret)) {
            return null;
        }
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }

        try {
            String data = secret + ":" + timestamp;
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
