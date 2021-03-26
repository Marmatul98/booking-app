package mmatula.bookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCalendarEvent {

    public long id;
    public int resourceId;
    public String title;
    public String start;
    public String end;
}
