package me.akshawop.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.model.UserOTP;
import me.akshawop.journalApp.model.UserSignup;
import me.akshawop.journalApp.model.ValidateOTPBody;
import me.akshawop.journalApp.service.EmailService;
import me.akshawop.journalApp.service.OTPService;
import me.akshawop.journalApp.service.RedisService;
import me.akshawop.journalApp.service.UserService;
import me.akshawop.journalApp.util.RegexPatterns;

@RestController
@Slf4j
@RequestMapping("/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private RedisService redis;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody UserSignup userData) {
        try {

            // email and password validation
            boolean isEmailValid = RegexPatterns.EMAIL.matcher(userData.getEmail()).find();
            boolean isPasswordValid = RegexPatterns.PASSWORD.matcher(userData.getPassword()).find();
            if (!(isEmailValid && isPasswordValid)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // check if the email is already registered
            if (userService.getUserByEmail(userData.getEmail()) != null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);

            // prepare user data for further processing and hash the password
            UserOTP userOtp = new UserOTP();
            userOtp.setEmail(userData.getEmail());
            userOtp.setPassword(passwordEncoder.encode(userData.getPassword()));

            // create and send otp to the user's email
            if (otpService.sendOTP(userOtp) == 0)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtpAndSaveUser(@RequestBody ValidateOTPBody body) {
        try {

            // check if email is valid
            boolean isEmailValid = RegexPatterns.EMAIL.matcher(body.email()).find();
            if (!isEmailValid)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // check if the email already registered
            if (userService.getUserByEmail(body.email()) != null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);

            // check if the email is waiting in redis for verification
            UserSignup tempUser = redis.get(body.email(), UserSignup.class);
            if (tempUser == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // validate the otp
            if (!otpService.validate(body.email(), body.code()))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            // save the new user to db and send confirmation mail
            User user = User.builder().email(tempUser.getEmail()).password(tempUser.getPassword()).build();
            if (userService.saveNewUser(user) == 0) {
                String username = userService.getUserByEmail(user.getEmail()).getUsername();
                emailService.sendSignupSuccessMail(user.getEmail(), username);

                return new ResponseEntity<>(HttpStatus.CREATED);
            } else
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("Exception in Signup controller: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
