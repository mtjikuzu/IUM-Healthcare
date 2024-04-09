package com.pluralsight.healthcare.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.SCrypt;

public class ScryptHasher {
    private static final Logger LOG = LoggerFactory.getLogger(ScryptHasher.class);

    public static void main(String[] args) {
        try {
            String password = "examplePassword";
            byte[] salt = getSalt();
            int N = 16384;
            int r = 8;
            int p = 1;
            int dkLen = 64; // Length of the generated key

            byte[] hash = scryptHash(password, salt, N, r, p, dkLen);
            LOG.info("SCrypt hash for {}: {}", password, toHex(hash));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public static byte[] scryptHash(String password, byte[] salt, int N, int r, int p, int dkLen) {
        // Converting the password string to a byte array
        byte[] passwordBytes = password.getBytes();
        // Using the SCrypt method
        return SCrypt.generate(passwordBytes, salt, N, r, p, dkLen);
    }

    private static byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
