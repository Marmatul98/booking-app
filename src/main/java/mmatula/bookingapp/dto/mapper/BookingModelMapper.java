package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.BookingDTO;
import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingModelMapper {

    private final SportsFieldModelMapper sportsFieldModelMapper;
    private final UserModelMapper userModelMapper;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Autowired
    public BookingModelMapper(SportsFieldModelMapper sportsFieldModelMapper, UserModelMapper userModelMapper, BookingRepository bookingRepository, BookingService bookingService) {
        this.sportsFieldModelMapper = sportsFieldModelMapper;
        this.userModelMapper = userModelMapper;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    public BookingDTO entityToDTO(Booking booking) {

        var user = booking.getUser();
        UserDTO userDTO = null;

        if (user != null) {
            userDTO = this.userModelMapper.entityToDto(user);
        }

        return new BookingDTO(
                booking.getId(),
                booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                booking.getBookedFrom().format(DateTimeFormatter.ofPattern("HH:mm")),
                booking.getBookedTo().format(DateTimeFormatter.ofPattern("HH:mm")),
                booking.isAvailable(),
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
