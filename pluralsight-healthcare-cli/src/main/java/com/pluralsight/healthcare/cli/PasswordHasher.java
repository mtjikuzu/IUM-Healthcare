package com.pluralsight.healthcare.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordHasher.class);

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String originalPassword = "securePassword!";
        String hashedPassword = passwordEncoder.encode(originalPassword);

        LOG.info("Original: {}", originalPassword);
        LOG.info("Hashed: {}", hashedPassword);

        String password = "UtCWg1#&4hdcU7k2";
        boolean passwordIsComplex = isPasswordComplex(password);
        if (passwordIsComplex) {
            LOG.info("{} is a complex password.", password);
        } else {
            LOG.warn("{} is not a complex password.", password);
        }
    }

    public static boolean isPasswordComplex(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return password.matches(regex);
    }
}