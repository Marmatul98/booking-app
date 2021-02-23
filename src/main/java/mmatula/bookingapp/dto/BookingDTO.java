package mmatula.bookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private long bookingId;
    private String bookedDate;
    private String startTime;
    private String endTime;

    private UserDTO user;

    private SportsFieldDTO sportsField;
}
