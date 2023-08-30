package com.weatherapp.utility;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
  public static void main(String[] args) {
    SecureRandom random = new SecureRandom();
    byte[] sharedSecret = new byte[64]; // 512 bits
    random.nextBytes(sharedSecret);
    String encodedSecret = Base64.getEncoder().encodeToString(sharedSecret);
    System.out.println("Your new 512-bit key: " + encodedSecret);
  }
}
