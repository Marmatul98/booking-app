package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.enums.ERole;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("This email already exists");
        }

        User user = new User(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                formatPhoneNumber(userDTO.getPhoneNumber()));
        user.setRole(ERole.USER);
        this.userRepository.save(user);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 9) {
            throw new IllegalArgumentException();
        }

        if (phoneNumber.length() == 9) {
            if (phoneNumber.matches("^\\d{9}$")) {
                return phoneNumber;
            } else throw new IllegalArgumentException();
        } else if (phoneNumber.length() == 13) {
            if (phoneNumber.matches("")){
                return null;
            }
        } else throw new IllegalArgumentException();
        return null;
    }
}
