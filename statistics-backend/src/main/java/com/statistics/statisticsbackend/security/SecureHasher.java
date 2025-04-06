package com.statistics.statisticsbackend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;


public class SecureHasher {

    private static final SecureRandom random = new SecureRandom();

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Calculate a secure hash from a password
     *
     * @param source input password
     * @param salt the salt generated for the User to which the password belongs
     * @return the hashed password
     */
    public static String secureHash(String source, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            // hash the source using the salted SHA hasher
            byte[] hashedResult = md.digest(source.getBytes(StandardCharsets.UTF_8));
            // convert the hashed result to a string
            return Base64.getEncoder().encodeToString(hashedResult);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize MessageDigest with SHA-512", e);
        }
    }
}
