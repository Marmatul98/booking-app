package mmatula.bookingapp.service;

import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUserId(long id){
        return this.userRepository.findById(id).orElseThrow();
    }

    public User getUserByEmail(String email){
        return this.userRepository.findByEmail(email);
    }
}
