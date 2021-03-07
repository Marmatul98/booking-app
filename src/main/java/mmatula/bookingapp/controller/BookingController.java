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
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;
    private final BookingModelMapper bookingModelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    public BookingController(BookingService bookingService, BookingModelMapper bookingModelMapper) {
        this.bookingService = bookingService;
        this.bookingModelMapper = bookingModelMapper;
    }

    @GetMapping("/api/booking")
    public List<Booking> getAllBookings() {
        return this.bookingService.getAllBookings();
    }

    @GetMapping("/api/booking/{sportsFieldId}/{day}/{month}/{year}")
    public List<BookingDTO> getBookingsBySportsFieldIdAndDate(@PathVariable int sportsFieldId, @PathVariable int day, @PathVariable int month, @PathVariable int year) {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getBookingsBySportsFieldIdAndDate(sportsFieldId, LocalDate.of(year, month, day)));
    }

    @GetMapping("/admin/requestedBookings")
    public List<BookingDTO> getRequestedBookings() {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getAllRequestedBookings());
    }

    @GetMapping("/admin/confirmedBookings")
    public List<BookingDTO> getConfirmedBookings() {
        return this.bookingModelMapper.entityListToDTOList(this.bookingService.getAllConfirmedBookings());
    }

    @GetMapping("/api/bookings/{id}")
    public List<BookingDTO> getBookingsBySportsFieldId(@PathVariable int id) {
        try {
            return bookingModelMapper.entityListToDTOList(bookingService.getBookingsBySportsFieldId(id));
        } catch (NoSuchElementException e) {
            //todo log
            throw new NoSuchElementException();
        }
    }

    @PostMapping("/admin/generateBookings")
    public void generateWeeklyBookings(@RequestBody BookingCreationRequest bookingCreationRequest) {
        try {
            this.bookingService.generateEmptyBookingsForDatesRange(bookingCreationRequest);
        } catch (Exception e) {
            //todo log
            throw e;
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

    @PutMapping("/api/booking/{bookingId}/{userId}")
    public void addUserToBooking(@PathVariable long bookingId, @PathVariable long userId) {
        try {
            this.bookingService.addUserToBooking(bookingId, userId);
        } catch (IllegalArgumentException e) {
            //todo log
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    @PutMapping("/api/booking/{bookingId}")
    public void requestBooking(@PathVariable long bookingId, @RequestBody UserDTO userDTO) {
        this.bookingService.requestBooking(bookingId, userDTO);
    }

    @PutMapping("/admin/booking/confirm/{bookingId}")
    public void confirmBooking(@PathVariable long bookingId) {
        this.bookingService.confirmBooking(bookingId);
    }

    @PutMapping("/admin/booking/remove/{bookingId}")
    public void removeBooking(@PathVariable long bookingId) {
        this.bookingService.removeBookingRequest(bookingId);
    }

    @DeleteMapping("/admin/booking")
    public void deleteAllBookings() {
        this.bookingService.deleteAllBookings();
    }
}
