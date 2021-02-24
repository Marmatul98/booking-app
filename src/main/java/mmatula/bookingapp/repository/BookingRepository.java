package mmatula.bookingapp.repository;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsBySportsFieldId(int id);

    List<Booking> getBookingsBySportsFieldIdAndDateOrderById(int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByRequestedTrueAndConfirmedFalse();

    List<Booking> getBookingsBySportsFieldAndDateAndBookedFromBetween(SportsField sportsField, LocalDate date, LocalTime from, LocalTime to);

    List<Booking> getBookingsByConfirmedTrueOrderByDate();
}
