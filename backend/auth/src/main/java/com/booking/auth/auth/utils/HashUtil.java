package com.booking.auth.auth.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtil {

  public static String generateSalt() {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  public static String hashPassword(String password, String salt) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(salt.getBytes());
    byte[] hashedPassword = md.digest(password.getBytes());
    return Base64.getEncoder().encodeToString(hashedPassword);
  }
}