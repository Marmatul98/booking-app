package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.exception.UsernameAlreadyExistsException;
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

    public void register(UserDTO userDTO) throws UsernameAlreadyExistsException {

        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()));
        this.userRepository.save(user);
    }
}