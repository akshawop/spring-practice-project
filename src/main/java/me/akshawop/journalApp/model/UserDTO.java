package me.akshawop.journalApp.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.akshawop.journalApp.util.RegexPatterns;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
        @NotBlank(message = "Email is required", groups = { OnSignup.class, OnLogin.class, OnOtpValidate.class })
        @Size(max = 100, message = "Email should not exceed 100 characters", groups = { OnSignup.class, OnLogin.class,
                        OnOtpValidate.class })
        @Email(regexp = RegexPatterns.EMAIL, message = "Invalid email format", groups = { OnSignup.class, OnLogin.class,
                        OnOtpValidate.class })
        String email;

        @Pattern(regexp = RegexPatterns.PASSWORD, message = "Password must be atleast 8 characters long and contain atleast 1 capital letter, 1 small letter, 1 digit and 1 special character", groups = {
                        OnSignup.class, OnLogin.class })
        String password;

        @NotBlank(message = "No code provided", groups = OnOtpValidate.class)
        @Length(min = 6, max = 6, message = "Code should be of 6 digits", groups = OnOtpValidate.class)
        String code;

        OTP otp;

        public static interface OnSignup {
        }

        public static interface OnOtpValidate {
        }

        public static interface OnLogin {
        }
}
