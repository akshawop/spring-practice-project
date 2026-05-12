package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.akshawop.journalApp.model.OTP;
import me.akshawop.journalApp.model.UserDTO;
import me.akshawop.journalApp.util.SecretGenerator;
import me.akshawop.journalApp.util.otp.OTPGenerator;
import me.akshawop.journalApp.util.otp.OTPHasher;

@Service
public class OTPService {

    @Autowired
    private RedisService redis;

    @Autowired
    private OTPGenerator otpGenerator;

    private static String secret = SecretGenerator.generateBase64Secret(32);

    public String getOTP(UserDTO user) {
        try {

            String otp = otpGenerator.generateOtp();
            OTP otpDTO = new OTP(OTPHasher.hmacSha256(otp, secret), "K0");
            user.setOtp(otpDTO);

            redis.set(user.getEmail(), user, 300l);
            return otp;

        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in OTP service", e);
        }
    }

    public boolean validate(String email, String otp) {
        try {

            UserDTO user = redis.get(email, UserDTO.class);
            if (user == null)
                return false;

            String otpHash = OTPHasher.hmacSha256(otp, secret);
            return OTPHasher.secureCompare(otpHash, user.getOtp().otpHash());

        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in OTP service", e);
        }
    }
}
