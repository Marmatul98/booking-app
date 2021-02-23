package mmatula.bookingapp.service;

import mmatula.bookingapp.exception.EntityUniqueNameAlreadyExistsException;
import mmatula.bookingapp.model.SportsField;
import mmatula.bookingapp.repository.SportsFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportsFieldService {

    private final SportsFieldRepository sportsFieldRepository;

    @Autowired
    public SportsFieldService(SportsFieldRepository sportsFieldRepository) {
        this.sportsFieldRepository = sportsFieldRepository;
    }

    public List<SportsField> getAllSportsFields() {
        return this.sportsFieldRepository.findAll();
    }

    public SportsField getSportsFieldById(int id) {
        return this.sportsFieldRepository.findById(id).orElseThrow();
    }

    public void addSportsFieldByName(String name) throws EntityUniqueNameAlreadyExistsException {

        if (this.sportsFieldRepository.findByName(name) != null) {
            throw new EntityUniqueNameAlreadyExistsException();
        }

        this.sportsFieldRepository.save(new SportsField(name));
    }

    public void addSportsField(SportsField sportsField) {
        this.sportsFieldRepository.save(sportsField);
    }

    public void deleteSportsFieldById(int id) {
        this.sportsFieldRepository.deleteById(id);
    }
}
