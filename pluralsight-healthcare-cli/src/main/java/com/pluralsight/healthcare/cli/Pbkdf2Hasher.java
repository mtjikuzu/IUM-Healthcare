package com.pluralsight.healthcare.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Pbkdf2Hasher {
    private static final Logger LOG = LoggerFactory.getLogger(Pbkdf2Hasher.class);

    public static void main(String[] args) {
        try {
            String password1 = "password123";
            String password2 = "helloWorld";
            String password3 = "javaSecure!";

            LOG.info("Hash for{}: {}", password1, generateStrongPasswordHash(password1));
            LOG.info("Hash for{}: {}", password2, generateStrongPasswordHash(password2));
            LOG.info("Hash for{}: {}", password3, generateStrongPasswordHash(password3));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error(e.getMessage());
        }
    }

    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
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
