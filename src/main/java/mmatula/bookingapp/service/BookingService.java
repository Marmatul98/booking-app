package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.repository.SportsFieldRepository;
import mmatula.bookingapp.repository.UserRepository;
import mmatula.bookingapp.request.EmptyBookingCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SportsFieldRepository sportsFieldRepository;
    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, SportsFieldRepository sportsFieldRepository, UserRepository userRepository, AuthenticationService authenticationService) {
        this.bookingRepository = bookingRepository;
        this.sportsFieldRepository = sportsFieldRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    public Booking getBookingById(long id) {
        return this.bookingRepository.findById(id).orElseThrow();
    }


    public void addOrUpdateBooking(Booking booking) {
        this.bookingRepository.save(booking);
    }

    public void generateEmptyBookingsForDatesRange(EmptyBookingCreationRequest emptyBookingCreationRequest) {

        SportsField sportsField = sportsFieldRepository.findById(emptyBookingCreationRequest.getSportsFieldId()).orElseThrow();

        List<LocalDate> dates = emptyBookingCreationRequest.getStartDate()
                .datesUntil(emptyBookingCreationRequest.getEndDate().plusDays(1))
                .collect(Collectors.toList());

        for (LocalDate date : dates) {
            createEmptyBookingsForSelectedDate(
                    date,
                    emptyBookingCreationRequest.getStartTime(),
                    emptyBookingCreationRequest.getEndTime(),
                    Duration.ofMinutes(emptyBookingCreationRequest.getDurationMinutes()),
                    sportsField);
        }
    }

    private void createEmptyBookingsForSelectedDate(
            LocalDate date, LocalTime startTime, LocalTime endTime, Duration bookingDuration, SportsField sportsField) {

        List<Booking> bookings = new ArrayList<>();

        Booking lastBooking = new Booking();
        lastBooking.setBookedFrom(startTime);
        lastBooking.setBookedTo(startTime.plus(bookingDuration));
        lastBooking.setDate(date);
        lastBooking.setSportsField(sportsField);
        bookings.add(lastBooking);

        while (lastBooking.getBookedTo().isBefore(endTime)) {
            Booking booking = new Booking();
            booking.setBookedFrom(lastBooking.getBookedTo());
            booking.setBookedTo(booking.getBookedFrom().plus(bookingDuration));
            booking.setDate(date);
            booking.setSportsField(sportsField);
            bookings.add(booking);
            lastBooking = booking;
        }

        bookingRepository.saveAll(bookings);
        sportsField.addBookings(new HashSet<>(bookings));
        sportsFieldRepository.save(sportsField);
    }

    public List<Booking> getBookingsBySportsFieldId(int id) {
        if (this.sportsFieldRepository.existsById(id)) {
            return this.bookingRepository.getBookingsBySportsFieldId(id);
        } else throw new NoSuchElementException();
    }

    public void addUserToBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getUser() == null) {
            booking.setUser(this.userRepository.findById(userId).orElseThrow());
            this.bookingRepository.save(booking);
        } else throw new IllegalArgumentException();
    }

    public List<Booking> getBookingsBySportsFieldIdAndDate(int sportsFieldId, LocalDate date) {
        return this.bookingRepository.getBookingsBySportsFieldIdAndDateOrderById(sportsFieldId, date);
    }

    public void requestBooking(long bookingId, UserDTO userDTO) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow();

        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user == null) {
            user = new User(
                    userDTO.getFirstName(),
                    userDTO.getLastName(),
                    userDTO.getEmail(),
                    null,
                    true
            );
        }
        booking.setRequested(true);
        booking.setConfirmed(false);
        booking.setUser(user);
        this.userRepository.save(user);
        this.bookingRepository.save(booking);
    }

    public void confirmBooking(long bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow();
        booking.setConfirmed(true);
        this.bookingRepository.save(booking);
    }

    public List<Booking> getAllRequestedBookings() {
        return this.bookingRepository.getBookingsByRequestedTrueAndConfirmedFalse();
    }

    public void removeBooking(long bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow();
        long userId = booking.getUser().getId();
        booking.setConfirmed(false);
        booking.setRequested(false);
        booking.setUser(null);
        this.bookingRepository.save(booking);
        this.userRepository.deleteById(userId);
    }
}
