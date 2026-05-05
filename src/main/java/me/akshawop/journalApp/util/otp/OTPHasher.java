package me.akshawop.journalApp.util.otp;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPHasher {

    public static String hmacSha256(String otp, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(key);

        byte[] rawHmac = mac.doFinal(otp.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    public static boolean secureCompare(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }
}