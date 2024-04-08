package com.pluralsight.healthcare.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    private static final Logger LOG = LoggerFactory.getLogger(PatientRetriever.class);

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String originalPassword = "securePassword!";
        String hashedPassword = passwordEncoder.encode(originalPassword);

        LOG.info("Original: {}", originalPassword);
        LOG.info("Hashed: {}", hashedPassword);
    }
}