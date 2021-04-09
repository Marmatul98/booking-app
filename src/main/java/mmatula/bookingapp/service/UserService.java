package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    public void updateUser(UserDTO userDTO) {
        User user = this.userRepository.findByEmail(userDTO.getEmail());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPhoneNumber(user.getPhoneNumber());
        this.userRepository.save(user);
    }
}
