package mmatula.bookingapp.repository;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;
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

    List<Booking> getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateBefore(String email, int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAndBookedFromBefore(String email, int sportsFieldId, LocalDate date, LocalTime time);

    List<Booking> getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAfter(String email, int sportsFieldId, LocalDate date);

    List<Booking> getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAndBookedFromAfter(String email, int sportsFieldId, LocalDate date, LocalTime time);

    List<Booking> getBookingsByUserAndRequestedTrueAndConfirmedFalseAndSportsField(User user, SportsField sportsField);

    List<Booking> getBookingsByUserAndConfirmedTrueAndSportsField(User user, SportsField sportsField);

    List<Booking> getBookingsByUserAndDateAndBookedFromBetweenAndSportsField(User user, LocalDate date, LocalTime startTime, LocalTime endTime, SportsField sportsField);

    Booking getBookingBySportsFieldAndDateAndBookedFrom(SportsField sportsField, LocalDate date, LocalTime time);
}
