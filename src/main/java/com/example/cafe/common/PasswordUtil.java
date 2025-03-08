package com.example.cafe.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component  // ✅ Bean으로 등록
public class PasswordUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 비밀번호 암호화
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    // 비밀번호 검증
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
