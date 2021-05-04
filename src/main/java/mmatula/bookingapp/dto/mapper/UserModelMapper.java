package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserModelMapper {

    public UserDTO entityToDto(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                "",
                user.getPhoneNumber()
        );

    }

    public List<UserDTO> entityListToDtoList(List<User> allUsers) {
        return allUsers
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
