package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.params.BookingCreationParams;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.repository.SportsFieldRepository;
import mmatula.bookingapp.repository.UserRepository;
import mmatula.bookingapp.request.BookingCreationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SportsFieldRepository sportsFieldRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepository bookingRepository, SportsFieldRepository sportsFieldRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.sportsFieldRepository = sportsFieldRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    public Booking getBookingById(long id) {
        return this.bookingRepository.findById(id).orElseThrow();
    }

    public void deleteAllBookings() {
        this.bookingRepository.deleteAll();
    }

    public void addOrUpdateBooking(Booking booking) {
        this.bookingRepository.save(booking);
    }

    public void generateEmptyBookingsForDatesRange(BookingCreationRequest bookingCreationRequest) {
        List<LocalDate> dates = getDatesUntilInclusive(bookingCreationRequest.getStartDate(), bookingCreationRequest.getEndDate());

        List<BookingCreationParams> paramsList = dates.stream()
                .map(date -> new BookingCreationParams.Builder()
                        .date(date)
                        .startTime(bookingCreationRequest.getStartTime())
                        .endTime(bookingCreationRequest.getEndTime())
                        .duration(Duration.ofMinutes(bookingCreationRequest.getDurationInMinutes()))
                        .sportsField(this.sportsFieldRepository.findById(bookingCreationRequest.getSportsFieldId()).orElseThrow())
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }


    private void createBookingsForSelectedDateAndTime(BookingCreationParams bookingCreationParams) {

        if (isSportsFieldFreeInDateAndTime(bookingCreationParams)) {

            SportsField sportsField = bookingCreationParams.getSportsField();
            User user = bookingCreationParams.getUser();

            List<Booking> bookings = new ArrayList<>();

            Booking lastBooking = new Booking();
            lastBooking.setBookedFrom(bookingCreationParams.getStartTime());
            lastBooking.setBookedTo(bookingCreationParams.getStartTime().plus(bookingCreationParams.getDuration()));
            lastBooking.setDate(bookingCreationParams.getDate());
            lastBooking.setSportsField(sportsField);
            lastBooking.setUser(user);
            lastBooking.setConfirmed(bookingCreationParams.getConfirmed());
            lastBooking.setRequested(bookingCreationParams.getRequested());
            bookings.add(lastBooking);

            while (lastBooking.getBookedTo().isBefore(bookingCreationParams.getEndTime())) {
                Booking booking = new Booking();
                booking.setBookedFrom(lastBooking.getBookedTo());
                booking.setBookedTo(booking.getBookedFrom().plus(bookingCreationParams.getDuration()));
                booking.setDate(bookingCreationParams.getDate());
                booking.setSportsField(sportsField);
                booking.setUser(user);
                booking.setRequested(bookingCreationParams.getRequested());
                booking.setConfirmed(bookingCreationParams.getConfirmed());
                bookings.add(booking);
                lastBooking = booking;
            }

            bookingRepository.saveAll(bookings);
        } else throw new IllegalArgumentException("Sports field is not free in selected date and time");
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

    public void removeBookingRequest(long bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow();
        User user = booking.getUser();

        booking.setConfirmed(false);
        booking.setRequested(false);
        booking.setUser(null);
        this.bookingRepository.save(booking);

        if (user.isGuest() && user.getBookings().isEmpty()) {
            this.userRepository.delete(user);
        } else this.userRepository.save(user);
    }

    public void addAdminBooking(BookingCreationRequest bookingCreationRequest) {
        List<LocalDate> dates = getDatesUntilInclusive(bookingCreationRequest.getStartDate(), bookingCreationRequest.getEndDate());

        List<BookingCreationParams> paramsList = dates.stream()
                .map(date -> new BookingCreationParams.Builder()
                        .date(date)
                        .startTime(bookingCreationRequest.getStartTime())
                        .endTime(bookingCreationRequest.getEndTime())
                        .duration(Duration.ofMinutes(bookingCreationRequest.getDurationInMinutes()))
                        .sportsField(this.sportsFieldRepository.findById(bookingCreationRequest.getSportsFieldId()).orElseThrow())
                        .requested(true)
                        .confirmed(true)
                        .user(this.userRepository.findById(1L).orElseThrow())
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }

    private boolean isSportsFieldFreeInDateAndTime(BookingCreationParams bookingCreationParams) {
        return this.bookingRepository.getBookingsBySportsFieldAndDateAndBookedFromBetween(
                bookingCreationParams.getSportsField(),
                bookingCreationParams.getDate(),
                bookingCreationParams.getStartTime(),
                bookingCreationParams.getEndTime()).isEmpty();
    }

    private List<LocalDate> getDatesUntilInclusive(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
    }

    public List<Booking> getAllConfirmedBookings() {
        return this.bookingRepository.getBookingsByConfirmedTrueOrderByDate();
    }

    @Scheduled(cron = "@daily")
    public void deleteNonRequestedBookings() {
        this.bookingRepository.getBookingsByRequestedFalseAndDateBefore(LocalDate.now())
                .forEach(booking -> {
                    logger.info("Deleting booking dated " + booking.getDate().toString());
                    this.bookingRepository.delete(booking);
                });
    }
}
