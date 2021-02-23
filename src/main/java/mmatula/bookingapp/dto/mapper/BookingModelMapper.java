package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.BookingDTO;
import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingModelMapper {

    private final SportsFieldModelMapper sportsFieldModelMapper;
    private final UserModelMapper userModelMapper;

    @Autowired
    public BookingModelMapper(SportsFieldModelMapper sportsFieldModelMapper, UserModelMapper userModelMapper) {
        this.sportsFieldModelMapper = sportsFieldModelMapper;
        this.userModelMapper = userModelMapper;
    }

    public BookingDTO entityToDTO(Booking booking) {

        User user = booking.getUser();
        UserDTO userDTO = null;

        if (user != null) {
            userDTO = this.userModelMapper.entityToDto(user);
        }

        return new BookingDTO(
                booking.getId(),
                booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                booking.getBookedFrom().format(DateTimeFormatter.ofPattern("HH:mm")),
                booking.getBookedTo().format(DateTimeFormatter.ofPattern("HH:mm")),
                userDTO,
                this.sportsFieldModelMapper.entityToDto(booking.getSportsField())
        );
    }

    public List<BookingDTO> entityListToDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }
}
