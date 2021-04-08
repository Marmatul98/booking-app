package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.enums.ERole;
import mmatula.bookingapp.model.PasswordResetToken;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.PasswordResetTokenRepository;
import mmatula.bookingapp.repository.UserRepository;
import mmatula.bookingapp.request.PasswordResetBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void register(UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("This email already exists");
        }

        User user = new User(
                userDTO.getFirstName().trim(),
                userDTO.getLastName().trim(),
                userDTO.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(userDTO.getPassword()),
                validatePhoneNumber(userDTO.getPhoneNumber()));
        user.setRole(ERole.USER);
        this.userRepository.save(user);
    }

    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 13 || !phoneNumber.matches("\\+[0-9]{12}")) {
            throw new IllegalArgumentException(phoneNumber);
        } else {
            return phoneNumber;
        }
    }

    public void requestPasswordReset(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                LocalDateTime.now().plusMinutes(15),
                user,
                UUID.randomUUID());

        this.passwordResetTokenRepository.save(passwordResetToken);

        this.emailService.sendEmail(
                user.getEmail(),
                "Reset hesla",
                "http://localhost:4200/password-reset?token=" + passwordResetToken.getToken().toString());
    }

    public void resetPassword(PasswordResetBody passwordResetBody) {
        PasswordResetToken passwordResetToken =
                this.passwordResetTokenRepository.findByToken(UUID.fromString(passwordResetBody.getToken()));

        if (passwordResetToken == null || passwordResetToken.getValid().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token is not found or expired");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetBody.getNewPassword()));
        this.userRepository.save(user);
        this.passwordResetTokenRepository.delete(passwordResetToken);
    }
}
