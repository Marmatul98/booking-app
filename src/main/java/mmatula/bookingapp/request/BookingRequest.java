package mmatula.bookingapp.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mmatula.bookingapp.dto.BookingDTO;
import mmatula.bookingapp.dto.UserDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingRequest {

    private UserDTO user;
    private List<BookingDTO> bookings;
}
