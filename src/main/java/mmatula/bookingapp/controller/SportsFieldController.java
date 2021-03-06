package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.SportsFieldDTO;
import mmatula.bookingapp.dto.mapper.SportsFieldModelMapper;
import mmatula.bookingapp.exception.EntityUniqueNameAlreadyExistsException;
import mmatula.bookingapp.service.BookingService;
import mmatula.bookingapp.service.SportsFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class SportsFieldController {

    private final SportsFieldService sportsFieldService;
    private final BookingService bookingService;
    private final SportsFieldModelMapper sportsFieldModelMapper;

    @Autowired
    public SportsFieldController(SportsFieldService sportsFieldService, BookingService bookingService, SportsFieldModelMapper sportsFieldModelMapper) {
        this.sportsFieldService = sportsFieldService;
        this.bookingService = bookingService;
        this.sportsFieldModelMapper = sportsFieldModelMapper;
    }

    @GetMapping("/sportsField")
    public List<SportsFieldDTO> getAllSportsFields() {
        return this.sportsFieldModelMapper.entityListToDtoList(this.sportsFieldService.getAllSportsFields());
    }
//
//    @GetMapping("/sportsField/{id}")
//    public SportsFieldDTO getSportsFieldById(@PathVariable int id) {
//        try {
//            SportsField sportsField = this.sportsFieldService.getSportsFieldById(id);
//            return this.sportsFieldModelMapper.entityToDTO(sportsField, this.bookingService.getBookingsBySportsFieldId(sportsField.getId()));
//        } catch (NoSuchElementException e) {
//            //todo log exception
//            throw new NoSuchElementException();
//        }
//    }

    @PostMapping("/sportsField")
    public void addSportsField(@RequestBody String name) {
        try {
            this.sportsFieldService.addSportsFieldByName(name);
        } catch (EntityUniqueNameAlreadyExistsException e) {
            //todo log exception
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/sportsField/{id}")
    public void deleteSportsFieldById(@PathVariable int id) {
        try {
            this.sportsFieldService.deleteSportsFieldById(id);
        } catch (Exception e) {
            //todo log
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/secret")
    public String getSecret() {
        return "Tajemstvi";
    }

}
