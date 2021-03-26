package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.SportsFieldDTO;
import mmatula.bookingapp.dto.mapper.SportsFieldModelMapper;
import mmatula.bookingapp.exception.EntityUniqueNameAlreadyExistsException;
import mmatula.bookingapp.service.SportsFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@CrossOrigin("*")
public class SportsFieldController {

    private final SportsFieldService sportsFieldService;
    private final SportsFieldModelMapper sportsFieldModelMapper;

    @Autowired
    public SportsFieldController(SportsFieldService sportsFieldService, SportsFieldModelMapper sportsFieldModelMapper) {
        this.sportsFieldService = sportsFieldService;
        this.sportsFieldModelMapper = sportsFieldModelMapper;
    }

    @GetMapping("/api/sportsField")
    public List<SportsFieldDTO> getAllSportsFields() {
        return this.sportsFieldModelMapper.entityListToDtoList(this.sportsFieldService.getAllSportsFields());
    }

    @PostMapping("/admin/sportsField")
    public void addSportsField(@RequestBody String name) {
        try {
            this.sportsFieldService.addSportsFieldByName(name);
        } catch (EntityUniqueNameAlreadyExistsException e) {
            //todo log exception
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/admin/sportsField/{id}")
    public void deleteSportsFieldById(@PathVariable int id) {
        try {
            this.sportsFieldService.deleteSportsFieldById(id);
        } catch (Exception e) {
            //todo log
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
