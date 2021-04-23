package mmatula.bookingapp.controller;

import mmatula.bookingapp.Util.BookingSortingUtil;
import mmatula.bookingapp.dto.BookingDTO;
import mmatula.bookingapp.dto.mapper.BookingModelMapper;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.request.BookingCreationRequest;
import mmatula.bookingapp.request.BookingRequest;
import mmatula.bookingapp.service.BookingService;
import mmatula.bookingapp.service.ExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;
    private final BookingModelMapper bookingModelMapper;
    private final ExceptionLogService exceptionLogService;
    private final BookingSortingUtil bookingSortingUtil;

    @Autowired
    public BookingController(BookingService bookingService, BookingModelMapper bookingModelMapper, ExceptionLogService exceptionLogService, BookingSortingUtil bookingSortingUtil) {
        this.bookingService = bookingService;
        this.bookingModelMapper = bookingModelMapper;
        this.exceptionLogService = exceptionLogService;
        this.bookingSortingUtil = bookingSortingUtil;
    }

    @GetMapping("/api/booking")
    public List<Booking> getAllBookings() {
        try {
            return this.bookingService.getAllBookings();
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/booking/{sportsFieldId}/{day}/{month}/{year}")
    public List<BookingDTO> getBookingsBySportsFieldIdAndDate(@PathVariable int sportsFieldId, @PathVariable int day, @PathVariable int month, @PathVariable int year) {
        try {
            return this.bookingModelMapper.entityListToDTOList(
                    this.bookingService.getBookingsBySportsFieldIdAndDate(sportsFieldId, LocalDate.of(year, month, day)));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/bookingSlots")
    public List<String> getAvailableBookingTimeSlots() {
        try {
            return this.bookingService.getAvailableBookingTimeSlots();
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/requestedBookings")
    public List<BookingDTO> getRequestedBookings() {
        try {
            return this.bookingModelMapper.entityListToDTOList(
                    this.bookingSortingUtil.sortByDateAndByTime(
                            this.bookingService.getAllRequestedBookings()));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/confirmedBookings")
    public List<BookingDTO> getConfirmedBookings() {
        try {
            return this.bookingModelMapper.entityListToDTOList(
                    this.bookingSortingUtil.sortByDateAndByTime(
                            this.bookingService.getAllConfirmedBookings()));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/pastUserBookings/{email}")
    public List<BookingDTO> getPastUserBookings(@PathVariable String email) {
        try {
            return this.bookingModelMapper.entityListToDTOList(this.bookingService.getPastBookingsByUserEmail(email));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/futureUserBookings/{email}")
    public List<BookingDTO> getFutureUserBookings(@PathVariable String email) {
        try {
            return this.bookingModelMapper.entityListToDTOList(this.bookingService.getFutureBookingsByUserEmail(email));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/bookings/{id}")
    public List<BookingDTO> getBookingsBySportsFieldId(@PathVariable int id) {
        try {
            return bookingModelMapper.entityListToDTOList(bookingService.getBookingsBySportsFieldId(id));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/generateBookings")
    public void generateEmptyBookings(@RequestBody BookingCreationRequest bookingCreationRequest) {
        try {
            this.bookingService.generateEmptyBookingsForDatesRange(bookingCreationRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/booking")
    public void addAdminBooking(@RequestBody BookingCreationRequest bookingCreationRequest) {
        try {
            this.bookingService.addAdminBooking(bookingCreationRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/api/booking/{bookingId}/{userId}")
    public void addUserToBooking(@PathVariable long bookingId, @PathVariable long userId) {
        try {
            this.bookingService.addUserToBooking(bookingId, userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/api/booking")
    public void requestBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            this.bookingService.requestBooking(bookingRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/booking/confirm/{bookingId}")
    public void confirmBooking(@PathVariable long bookingId) {
        try {
            this.bookingService.confirmBooking(bookingId);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/booking/remove/{bookingId}")
    public void removeBooking(@PathVariable long bookingId) {
        try {
            this.bookingService.removeBookingRequest(bookingId);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/booking")
    public void deleteAllBookings() {
        try {
            this.bookingService.deleteAllBookings();
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
