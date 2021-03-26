package mmatula.bookingapp.dto.mapper;

import mmatula.bookingapp.dto.BookingCalendarEvent;
import mmatula.bookingapp.model.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingCalendarEventModelMapper {

    public BookingCalendarEvent entityToDTO(Booking booking) {
        return new BookingCalendarEvent(
                booking.getId(),
                booking.getSportsField().getId(),
                booking.getRequested() ? "Rezervano" : "Volno",
                parseToDateTime(booking.getDate(), booking.getBookedFrom()),
                parseToDateTime(booking.getDate(), booking.getBookedTo())
        );
    }

    public List<BookingCalendarEvent> entityListToDTOList(List<Booking> bookings) {
        return bookings
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    private String parseToDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
