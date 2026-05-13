package me.akshawop.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.exception.DuplicateUserRegistrationException;
import me.akshawop.journalApp.exception.GenericNotFoundException;
import me.akshawop.journalApp.exception.OTPValidationFailedException;
import me.akshawop.journalApp.model.UserDTO;
import me.akshawop.journalApp.service.EmailService;
import me.akshawop.journalApp.service.OTPService;
import me.akshawop.journalApp.service.RedisService;
import me.akshawop.journalApp.service.UserService;

@RestController
@RequestMapping("/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    @Autowired
    private RedisService redis;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaTemplate<String, User> kafka;

    @PostMapping
    public ResponseEntity<HttpStatus> signup(@Validated(UserDTO.OnSignup.class) @RequestBody UserDTO userData) {

        // check if the email is already registered
        if (userService.getUserByEmail(userData.getEmail()) != null)
            throw new DuplicateUserRegistrationException("This email is already registered");

        // encode the password before saving to redis
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));

        // generate and send otp to the user's email for verification
        String otp = otpService.getOTP(userData);
        emailService.sendOTPVerificationMail(userData.getEmail(), otp);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<User> validateOtpAndSaveUser(
            @Validated(UserDTO.OnOtpValidate.class) @RequestBody UserDTO body) {

        // check if the email already registered
        if (userService.getUserByEmail(body.getEmail()) != null)
            throw new DuplicateUserRegistrationException("This email is already registered");

        // check if the email is waiting in redis for verification
        UserDTO tempUser = redis.get(body.getEmail(), UserDTO.class);
        if (tempUser == null)
            throw new GenericNotFoundException("Not such email found to be waiting for verification");

        // validate the otp
        if (!otpService.validate(body.getEmail(), body.getCode()))
            throw new OTPValidationFailedException("Incorrect OTP provided");

        // save the new user to db and send confirmation mail
        User savedUser = userService.saveNewUser(tempUser);

        kafka.send("user.account.created", savedUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
