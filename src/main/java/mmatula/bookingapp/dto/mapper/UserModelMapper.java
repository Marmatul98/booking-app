package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserModelMapper {

    public UserDTO entityToDto(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.isGuest()
        );
    }
}
