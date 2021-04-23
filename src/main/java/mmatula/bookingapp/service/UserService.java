package mmatula.bookingapp.service;

import mmatula.bookingapp.Util.PhoneNumberUtil;
import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PhoneNumberUtil phoneNumberUtil;

    @Autowired
    public UserService(UserRepository userRepository, PhoneNumberUtil phoneNumberUtil) {
        this.userRepository = userRepository;
        this.phoneNumberUtil = phoneNumberUtil;
    }

    public User getUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    public void updateUser(UserDTO userDTO) {
        User user = getUserByEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName().trim());
        user.setLastName(userDTO.getLastName().trim());
        user.setPhoneNumber(this.phoneNumberUtil.validatePhoneNumber(userDTO.getPhoneNumber().trim()));
        this.userRepository.save(user);
    }
}
