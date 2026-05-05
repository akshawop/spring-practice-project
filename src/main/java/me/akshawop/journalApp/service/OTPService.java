package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.akshawop.journalApp.dto.OTP;
import me.akshawop.journalApp.dto.UserOTP;
import me.akshawop.journalApp.util.SecretGenerator;
import me.akshawop.journalApp.util.otp.OTPGenerator;
import me.akshawop.journalApp.util.otp.OTPHasher;

@Service
@Slf4j
public class OTPService {

    @Autowired
    private RedisService redis;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPGenerator otpGenerator;

    private static String secret = SecretGenerator.generateBase64Secret(32);

    public int sendOTP(UserOTP user) {
        try {
            String otp = otpGenerator.generateOtp();
            OTP otpDTO = new OTP(OTPHasher.hmacSha256(otp, secret), "K0");
            user.setOtp(otpDTO);

            redis.set(user.getEmail(), user, 300l);
            emailService.sendOTP(user.getEmail(), otp);
            return 0;
        } catch (Exception e) {
            log.error("Exception in OTP service: ", e);
            return 1;
        }
    }

    public boolean validate(String email, String otp) {
        try {
            UserOTP user = redis.get(email, UserOTP.class);
            if (user == null)
                return false;

            String otpHash = OTPHasher.hmacSha256(otp, secret);
            return OTPHasher.secureCompare(otpHash, user.getOtp().otpHash());
        } catch (Exception e) {
            log.error("Exception in OTP service: ", e);
            return false;
        }
    }
}
