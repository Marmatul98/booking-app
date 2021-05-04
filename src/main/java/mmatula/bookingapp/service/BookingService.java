package mmatula.bookingapp.service;

import mmatula.bookingapp.dto.BookingDTO;
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

    private static final String TIME_FORMAT = "HH:mm";

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
                        .duration(Duration.ofMinutes(30))
                        .sportsFields(this.sportsFieldRepository.findByIdIn(bookingCreationRequest.getSportsFieldIds()))
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }

    public List<Booking> getBookingsBySportsFieldId(int id) {
        if (this.sportsFieldRepository.existsById(id)) {
            return this.bookingRepository.getBookingsBySportsFieldId(id);
        } else throw new NoSuchElementException();
    }

    public void addUserToBooking(long bookingId, long userId) {
        var booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getUser() == null) {
            booking.setUser(this.userRepository.findById(userId).orElseThrow());
            this.bookingRepository.save(booking);
        } else throw new IllegalArgumentException();
    }

    public List<Booking> getBookingsBySportsFieldIdAndDate(int sportsFieldId, LocalDate date) {
        return this.bookingRepository.getBookingsBySportsFieldIdAndDateOrderById(sportsFieldId, date);
    }

    public void requestBooking(BookingRequest bookingRequest) {
        var user = userService.getUserByEmail(bookingRequest.getUser().getEmail());
        userService.updateUser(bookingRequest.getUser());

        bookingRequest.getBookings()
                .forEach(bookingDTO -> {
                    var booking = this.bookingRepository.findById(bookingDTO.getBookingId()).orElseThrow();
                    if (!booking.getConfirmed() && !booking.isRequested() && booking.getUser() == null) {
                        booking.setUser(user);
                        booking.setRequested(true);
                        booking.setConfirmed(false);
                        this.userRepository.save(user);
                        this.bookingRepository.save(booking);
                    } else throw new IllegalArgumentException("Booking is occupied");
                });
    }

    public void confirmGroupedBookings(BookingDTO bookingDTO) {
        List<Booking> bookingsToConfirm = getBookingsInTimeInterval(bookingDTO);
        for (Booking booking : bookingsToConfirm) {
            booking.setConfirmed(true);
            this.bookingRepository.save(booking);
        }
        this.emailService.sendConfirmationEmail(bookingDTO);
    }

    public void removeGroupedBookingsRequest(BookingDTO bookingDTO) {
        List<Booking> bookingsToRemove = getBookingsInTimeInterval(bookingDTO);
        for (Booking booking : bookingsToRemove) {
            var user = booking.getUser();

            booking.setConfirmed(false);
            booking.setRequested(false);
            booking.setUser(null);

            this.bookingRepository.save(booking);
            this.userRepository.save(user);
        }
        this.emailService.sendRemovalEmail(bookingDTO);
    }

    public List<Booking> getAllRequestedBookings() {
        List<Booking> requestedBookings = this.bookingRepository.getBookingsByRequestedTrueAndConfirmedFalse();

        Set<User> users = requestedBookings
                .stream()
                .map(Booking::getUser)
                .collect(Collectors.toSet());

        List<Booking> returnedList = new ArrayList<>();

        for (User user : users) {
            List<Booking> requestedUserBookings = this.bookingRepository.getBookingsByUserAndRequestedTrueAndConfirmedFalse(user);
            returnedList.addAll(this.groupSameDayBookings(requestedUserBookings));
        }
        return returnedList;
    }

    public void addAdminBooking(BookingCreationRequest bookingCreationRequest) {
        List<LocalDate> dates = getDatesUntilInclusive(bookingCreationRequest.getStartDate(), bookingCreationRequest.getEndDate());

        List<BookingCreationParams> paramsList = dates.stream()
                .map(date -> new BookingCreationParams.Builder()
                        .date(date)
                        .startTime(bookingCreationRequest.getStartTime())
                        .endTime(bookingCreationRequest.getEndTime())
                        .duration(Duration.ofMinutes(30))
                        .sportsFields(this.sportsFieldRepository.findByIdIn(bookingCreationRequest.getSportsFieldIds()))
                        .requested(true)
                        .confirmed(true)
                        .user(this.userRepository.findById(1L).orElseThrow())
                        .build())
                .collect(Collectors.toList());

        paramsList.forEach(this::createBookingsForSelectedDateAndTime);
    }

    public List<Booking> getAllConfirmedBookings() {
        List<Booking> requestedBookings = this.bookingRepository.getBookingsByConfirmedTrueOrderByDate();

        Set<User> users = requestedBookings
                .stream()
                .map(Booking::getUser)
                .collect(Collectors.toSet());

        List<Booking> returnedList = new ArrayList<>();

        for (User user : users) {
            List<Booking> requestedUserBookings = this.bookingRepository.getBookingsByUserAndConfirmedTrue(user);
            returnedList.addAll(this.groupSameDayBookings(requestedUserBookings));
        }
        return returnedList;

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
        var startTime = LocalTime.of(8, 0);
        var endTime = LocalTime.of(22, 0);

        Deque<LocalTime> timeDeque = new ArrayDeque<>();
        timeDeque.add(startTime);

        while (true) {
            assert timeDeque.peekLast() != null;
            if (!timeDeque.peekLast().isBefore(endTime)) break;
            timeDeque.add(timeDeque.peekLast().plusMinutes(30));
        }

        var formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

        List<String> returnedList = new ArrayList<>();
        List<LocalTime> dequeAsList = new ArrayList<>(timeDeque);
        for (var i = 1; i < dequeAsList.size(); i++) {
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

    private List<LocalDate> getDatesUntilInclusive(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
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

    private void createBookingsForSelectedDateAndTime(BookingCreationParams bookingCreationParams) {
        if (areSportsFieldsNonExistentInDateAndTime(bookingCreationParams)) {
            for (SportsField sportsField : bookingCreationParams.getSportsFields()) {
                var user = bookingCreationParams.getUser();

                List<Booking> bookings = new ArrayList<>();

                var lastBooking = new Booking();
                lastBooking.setBookedFrom(bookingCreationParams.getStartTime());
                lastBooking.setBookedTo(bookingCreationParams.getStartTime().plus(bookingCreationParams.getDuration()));
                lastBooking.setDate(bookingCreationParams.getDate());
                lastBooking.setSportsField(sportsField);
                lastBooking.setUser(user);
                lastBooking.setConfirmed(bookingCreationParams.getConfirmed());
                lastBooking.setRequested(bookingCreationParams.getRequested());
                bookings.add(lastBooking);

                while (lastBooking.getBookedTo().isBefore(bookingCreationParams.getEndTime())) {
                    var booking = new Booking();
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

    /**
     * This method create special booking objects from bookings that can be grouped by time.
     * Example: 18:00-18:30 and 18:30-19:00 is merged to 18:00-19:00
     * This method must be prettified in the future
     */
    private List<Booking> groupTimesInBookings(List<Booking> bookings) {
        bookings.sort(Comparator.comparing(Booking::getBookedFrom));

        List<Integer> indexes = getIndexesForSublist(bookings);
        List<Booking> groupedBookings = new ArrayList<>();

        if (indexes.isEmpty()) {
            groupedBookings.add(createBookingWithGroupedBookingTimes(bookings));
        } else {
            for (var i = 0; i < indexes.size(); i++) {

                if (indexes.size() == 1) {
                    groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(0, indexes.get(i) + 1)));
                    groupedBookings.add(createBookingWithGroupedBookingTimes(bookings.subList(indexes.get(i) + 1, bookings.size())));
                    break;
                }

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

        for (var i = 0; i < bookings.size(); i++) {
            if (i + 1 < bookings.size() && !bookings.get(i).getBookedTo().equals(bookings.get(i + 1).getBookedFrom())) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private Booking createBookingWithGroupedBookingTimes(List<Booking> bookings) {
        var booking = new Booking();
        booking.setId(null);
        booking.setDate(bookings.get(0).getDate());
        booking.setSportsField(bookings.get(0).getSportsField());
        booking.setUser(bookings.get(0).getUser());
        booking.setBookedFrom(bookings.get(0).getBookedFrom());
        booking.setBookedTo(bookings.get(bookings.size() - 1).getBookedTo());
        return booking;
    }

    private List<Booking> getBookingsInTimeInterval(BookingDTO bookingDTO) {
        var user = this.userRepository.findByEmail(bookingDTO.getUser().getEmail());
        var date = LocalDate.parse(bookingDTO.getBookedDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        var startTime = LocalTime.parse(bookingDTO.getStartTime(), DateTimeFormatter.ofPattern(TIME_FORMAT));
        var endTime = LocalTime.parse(bookingDTO.getEndTime(), DateTimeFormatter.ofPattern(TIME_FORMAT));
        return this.bookingRepository.getBookingsByUserAndDateAndBookedFromBetween(
                user, date, startTime, endTime);
    }
}
