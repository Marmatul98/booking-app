package mmatula.bookingapp.Util;

import mmatula.bookingapp.model.Booking;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingSortingUtil {

    public List<Booking> sortByDateAndByTime(List<Booking> bookings) {
        Comparator<Booking> comparator = Comparator.comparing(Booking::getDate);
        comparator = comparator.thenComparing(Booking::getBookedFrom);
        return bookings
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
