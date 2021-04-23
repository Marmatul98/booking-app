package mmatula.bookingapp.service;

import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.model.User;
import mmatula.bookingapp.params.BookingCreationParams;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.repository.SportsFieldRepository;
import mmatula.bookingapp.repository.UserRepository;
import mmatula.bookingapp.request.BookingCreationRequest;
import mmatula.bookingapp.request.BookingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SportsFieldRepository sportsFieldRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepository bookingRepository, SportsFieldRepository sportsFieldRepository, UserRepository userRepository, EmailService emailService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.sportsFieldRepository = sportsFieldRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    public void deleteAllBookings() {
        this.bookingRepository.deleteAll();
    }

    public void generateEmptyBookingsForDatesRange(BookingCreationRequest bookingCreationRequest) {
        List<LocalDate> dates = getDatesUntilInclusive(bookingCreationRequest.getStartDate(), bookingCreationRequest.getEndDate());

        List<BookingCreationParams> paramsList = dates.stream()
                .map(date -> new BookingCreationParams.Builder()
                        .date(date)
                        .startTime(bookingCreationRequest.getStartTime())
                        .endTime(bookingCreationRequest.getEndTime())
                        .duration(Duration.ofMinutes(bookingCreationRequest.getDurationInMinutes()))
                        .sportsFields(this.sportsFieldRepository.findByIdIn(bookingCreationRequest.getSportsFieldIds()))
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }

    private void createBookingsForSelectedDateAndTime(BookingCreationParams bookingCreationParams) {
        if (areSportsFieldsNonExistentInDateAndTime(bookingCreationParams)) {
            for (SportsField sportsField : bookingCreationParams.getSportsFields()) {
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
            }
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

    public void requestBooking(BookingRequest bookingRequest) {
        User user = userService.getUserByEmail(bookingRequest.getUser().getEmail());
        userService.updateUser(bookingRequest.getUser());

        bookingRequest.getBookings()
                .forEach(bookingDTO -> {
                    Booking booking = this.bookingRepository.findById(bookingDTO.getBookingId()).orElseThrow();
                    if (!booking.getConfirmed() && !booking.isRequested() && booking.getUser() == null) {
                        booking.setUser(user);
                        booking.setRequested(true);
                        booking.setConfirmed(false);
                        this.userRepository.save(user);
                        this.bookingRepository.save(booking);
                    } else throw new IllegalArgumentException("Booking is occupied");
                });
    }

    public void confirmBooking(long bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow();
        booking.setConfirmed(true);
        this.bookingRepository.save(booking);
        this.emailService.sendConfirmationEmail(booking);
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
        this.userRepository.save(user);
    }

    public void addAdminBooking(BookingCreationRequest bookingCreationRequest) {
        List<LocalDate> dates = getDatesUntilInclusive(bookingCreationRequest.getStartDate(), bookingCreationRequest.getEndDate());

        List<BookingCreationParams> paramsList = dates.stream()
                .map(date -> new BookingCreationParams.Builder()
                        .date(date)
                        .startTime(bookingCreationRequest.getStartTime())
                        .endTime(bookingCreationRequest.getEndTime())
                        .duration(Duration.ofMinutes(bookingCreationRequest.getDurationInMinutes()))
                        .sportsFields(this.sportsFieldRepository.findByIdIn(bookingCreationRequest.getSportsFieldIds()))
                        .requested(true)
                        .confirmed(true)
                        .user(this.userRepository.findById(1L).orElseThrow())
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }

    private boolean areSportsFieldsNonExistentInDateAndTime(BookingCreationParams bookingCreationParams) {
        for (SportsField sportsField : bookingCreationParams.getSportsFields()) {
            if (!this.bookingRepository.getBookingsBySportsFieldAndDateAndBookedFromBetween(
                    sportsField,
                    bookingCreationParams.getDate(),
                    bookingCreationParams.getStartTime(),
                    bookingCreationParams.getEndTime())
                    .isEmpty()) {
                return false;
            }
        }
        return true;
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

    public List<String> getAvailableBookingTimeSlots() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(22, 0);

        Deque<LocalTime> timeDeque = new ArrayDeque<>();
        timeDeque.add(startTime);

        while (true) {
            assert timeDeque.peekLast() != null;
            if (!timeDeque.peekLast().isBefore(endTime)) break;
            timeDeque.add(timeDeque.peekLast().plusMinutes(30));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        List<String> returnedList = new ArrayList<>();
        List<LocalTime> dequeAsList = new ArrayList<>(timeDeque);
        for (int i = 1; i < dequeAsList.size(); i++) {
            returnedList.add(dequeAsList.get(i - 1).format(formatter) + "-" + dequeAsList.get(i).format(formatter));
        }

        return returnedList;
    }

    public List<Booking> getFutureBookingsByUserEmail(String email) {
        List<Booking> returnedBookings = new ArrayList<>();
        for (SportsField sportsField : this.sportsFieldRepository.findAll()) {
            returnedBookings.addAll(
                    groupSameDayBookings(
                            this.bookingRepository.getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAndBookedFromAfter(
                                    email, sportsField.getId(), LocalDate.now(), LocalTime.now()
                            )
                    )
            );
            returnedBookings.addAll(
                    groupSameDayBookings(
                            this.bookingRepository.getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAfter(
                                    email, sportsField.getId(), LocalDate.now()
                            )
                    )
            );
        }
        returnedBookings.sort(Comparator.comparing(Booking::getDate));
        return returnedBookings;
    }

    public List<Booking> getPastBookingsByUserEmail(String email) {
        List<Booking> returnedBookings = new ArrayList<>();
        for (SportsField sportsField : this.sportsFieldRepository.findAll()) {
            returnedBookings.addAll(
                    groupSameDayBookings(
                            this.bookingRepository.getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateAndBookedFromBefore(
                                    email, sportsField.getId(), LocalDate.now(), LocalTime.now()
                            )
                    )
            );
            returnedBookings.addAll(
                    groupSameDayBookings(
                            this.bookingRepository.getBookingsByUserEmailAndConfirmedTrueAndSportsFieldIdAndDateBefore(
                                    email, sportsField.getId(), LocalDate.now()
                            )
                    )
            );
        }
        returnedBookings.sort(Comparator.comparing(Booking::getDate));
        return returnedBookings;
    }

    private List<Booking> groupSameDayBookings(List<Booking> bookings) {
        Set<LocalDate> dates = bookings
                .stream()
                .map(Booking::getDate)
                .collect(Collectors.toSet());

        List<Booking> returned = new ArrayList<>();

        for (LocalDate date : dates) {
            List<Booking> tempBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (booking.getDate().equals(date)) {
                    tempBookings.add(booking);
                }
            }
            returned.addAll(groupTimesInBookings(tempBookings));
        }

        return returned;
    }

    private List<Booking> groupTimesInBookings(List<Booking> bookings) {
        bookings.sort(Comparator.comparing(Booking::getBookedFrom));

        List<Integer> indexes = getIndexesForSublist(bookings);
        List<Booking> groupedBookings = new ArrayList<>();

        if (indexes.isEmpty()) {
            groupedBookings.add(createBookingWithGroupedBookingTimes(bookings));
        } else {
            for (int i = 0; i < indexes.size(); i++) {

                //start of indexes
                if (i == 0) {
                    groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(0, indexes.get(i) + 1)));
                    continue;
                }

                //end of indexes
                if (i + 1 == indexes.size()) {
                    groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(indexes.get(i - 1) + 1, indexes.get(i) + 1)));
                    groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(indexes.get(i) + 1, bookings.size())));
                    continue;
                }

                groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(indexes.get(i - 1) + 1, indexes.get(i) + 1)));
            }
        }

        return groupedBookings;
    }

    private List<Integer> getIndexesForSublist(List<Booking> bookings) {
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < bookings.size(); i++) {
            if (i + 1 < bookings.size() && !bookings.get(i).getBookedTo().equals(bookings.get(i + 1).getBookedFrom())) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private Booking createBookingWithGroupedBookingTimes(List<Booking> bookings) {
        Booking booking = new Booking();
        booking.setId(null);
        booking.setDate(bookings.get(0).getDate());
        booking.setSportsField(bookings.get(0).getSportsField());
        booking.setUser(bookings.get(0).getUser());
        booking.setBookedFrom(bookings.get(0).getBookedFrom());
        booking.setBookedTo(bookings.get(bookings.size() - 1).getBookedTo());
        return booking;
    }
}
