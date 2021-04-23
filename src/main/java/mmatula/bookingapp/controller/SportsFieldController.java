package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.SportsFieldDTO;
import mmatula.bookingapp.dto.mapper.SportsFieldModelMapper;
import mmatula.bookingapp.exception.EntityUniqueNameAlreadyExistsException;
import mmatula.bookingapp.service.ExceptionLogService;
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
    private final ExceptionLogService exceptionLogService;

    @Autowired
    public SportsFieldController(SportsFieldService sportsFieldService, SportsFieldModelMapper sportsFieldModelMapper, ExceptionLogService exceptionLogService) {
        this.sportsFieldService = sportsFieldService;
        this.sportsFieldModelMapper = sportsFieldModelMapper;
        this.exceptionLogService = exceptionLogService;
    }

    @GetMapping("/api/sportsField")
    public List<SportsFieldDTO> getAllSportsFields() {
        try {
            return this.sportsFieldModelMapper.entityListToDtoList(this.sportsFieldService.getAllSportsFields());
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/sportsField")
    public void addSportsField(@RequestBody String name) {
        try {
            this.sportsFieldService.addSportsField(name);
        } catch (EntityUniqueNameAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/sportsField/{id}")
    public void deleteSportsFieldById(@PathVariable int id) {
        try {
            this.sportsFieldService.deleteSportsFieldById(id);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
