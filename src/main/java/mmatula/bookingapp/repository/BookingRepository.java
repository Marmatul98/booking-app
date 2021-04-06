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

    List<Booking> getBookingsByDate(LocalDate date);

    List<Booking> getBookingsByConfirmedTrueOrderByDate();

    List<Booking> getBookingsByRequestedFalseAndDateBefore(LocalDate date);

    List<Booking> getBookingsByUserId(long userId);

    List<Booking> getBookingsByUserIdAndSportsFieldIdAndDateBefore(long userId, int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByUserIdAndSportsFieldIdAndDateAndBookedFromBefore(long userId, int sportsFieldId, LocalDate date, LocalTime time);

    List<Booking> getBookingsByUserIdAndSportsFieldIdAndDateAfter(long userId, int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByUserIdAndSportsFieldIdAndDateAndBookedFromAfter(long userId, int sportsFieldId, LocalDate date, LocalTime time);
}
