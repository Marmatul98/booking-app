package mmatula.bookingapp.service;

import mmatula.bookingapp.exception.EntityUniqueNameAlreadyExistsException;
import mmatula.bookingapp.model.Booking;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.repository.BookingRepository;
import mmatula.bookingapp.repository.SportsFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SportsFieldService {

    private final SportsFieldRepository sportsFieldRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public SportsFieldService(SportsFieldRepository sportsFieldRepository, BookingRepository bookingRepository) {
        this.sportsFieldRepository = sportsFieldRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<SportsField> getAllSportsFields() {
        return this.sportsFieldRepository.findAll();
    }

    public void addSportsField(String name) throws EntityUniqueNameAlreadyExistsException {

        if (this.sportsFieldRepository.findByName(name) != null) {
            throw new EntityUniqueNameAlreadyExistsException();
        }

        this.sportsFieldRepository.save(new SportsField(name));
    }

    public void deleteSportsFieldById(int id) {
        List<Booking> bookings = new ArrayList<>(this.bookingRepository.getBookingsBySportsFieldId(id));
        for (Booking booking : bookings) {
            this.bookingRepository.delete(booking);
        }
        this.sportsFieldRepository.deleteById(id);
    }
}
