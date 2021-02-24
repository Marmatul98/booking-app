package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.BookingDTO;
import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.dto.mapper.BookingModelMapper;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.request.BookingCreationRequest;
import mmatula.bookingapp.service.BookingService;
import mmatula.bookingapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final BookingModelMapper bookingModelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService, BookingModelMapper bookingModelMapper, PasswordEncoder passwordEncoder) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.bookingModelMapper = bookingModelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/booking")
    public List<Booking> getAllBookings() {
        return this.bookingService.getAllBookings();
    }

    @GetMapping("/booking/{sportsFieldId}/{day}/{month}/{year}")
    public List<BookingDTO> getBookingsBySportsFieldIdAndDate(@PathVariable int sportsFieldId, @PathVariable int day, @PathVariable int month, @PathVariable int year) {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getBookingsBySportsFieldIdAndDate(sportsFieldId, LocalDate.of(year, month, day)));
    }

    @GetMapping("/requestedBookings")
    public List<BookingDTO> getRequestedBookings() {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getAllRequestedBookings());
    }

    @GetMapping("/confirmedBookings")
    public List<BookingDTO> getConfirmedBookings() {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getAllConfirmedBookings());
    }

    @GetMapping("/bookings/{id}")
    public List<BookingDTO> getBookingsBySportsFieldId(@PathVariable int id) {
        try {
            return bookingModelMapper.entityListToDTOList(bookingService.getBookingsBySportsFieldId(id));
        } catch (NoSuchElementException e) {
            //todo log
            throw new NoSuchElementException();
        }
    }

    @PostMapping("/booking")
    public void addBooking(@RequestBody Booking booking) {
        this.bookingService.addOrUpdateBooking(booking);
    }

    @PostMapping("/generateBookings")
    public void generateWeeklyBookings(@RequestBody BookingCreationRequest bookingCreationRequest) {
        try {
            this.bookingService.generateEmptyBookingsForDatesRange(bookingCreationRequest);
        } catch (Exception e) {
            //todo log
        }
    }

    @PostMapping("/admin/booking")
    public void addAdminBooking(@RequestBody BookingCreationRequest bookingCreationRequest) {
        try {
            this.bookingService.addAdminBooking(bookingCreationRequest);
        } catch (IllegalArgumentException e) {
            //todo log
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/booking/{bookingId}/{userId}")
    public void addUserToBooking(@PathVariable long bookingId, @PathVariable long userId) {
        try {
            this.bookingService.addUserToBooking(bookingId, userId);
        } catch (IllegalArgumentException e) {
            //todo log
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    @PutMapping("/booking/{bookingId}")
    public void requestBooking(@PathVariable long bookingId, @RequestBody UserDTO userDTO) {
        this.bookingService.requestBooking(bookingId, userDTO);
    }

    @PutMapping("/booking/confirm/{bookingId}")
    public void confirmBooking(@PathVariable long bookingId) {
        this.bookingService.confirmBooking(bookingId);
    }

    @PutMapping("/booking/remove/{bookingId}")
    public void removeBooking(@PathVariable long bookingId) {
        this.bookingService.removeBookingRequest(bookingId);
    }

    @DeleteMapping("/booking")
    public void deleteAllBookings() {
        this.bookingService.deleteAllBookings();
    }
}
