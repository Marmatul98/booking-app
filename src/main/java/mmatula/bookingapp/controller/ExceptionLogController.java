package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.ExceptionLogDTO;
import mmatula.bookingapp.dto.mapper.ExceptionLogModelMapper;
import mmatula.bookingapp.service.ExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/admin")
public class ExceptionLogController {

    private final ExceptionLogService exceptionLogService;
    private final ExceptionLogModelMapper exceptionLogModelMapper;

    @Autowired
    public ExceptionLogController(ExceptionLogService exceptionLogService, ExceptionLogModelMapper exceptionLogModelMapper) {
        this.exceptionLogService = exceptionLogService;
        this.exceptionLogModelMapper = exceptionLogModelMapper;
    }

    @GetMapping("/exceptionLog")
    public List<ExceptionLogDTO> getAllExceptionLogs() {
        try {
            return this.exceptionLogModelMapper.entityListToDtoList(this.exceptionLogService.getAllExceptionLogs());
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/exceptionLog/{id}")
    public void deleteExceptionLogById(@PathVariable long id) {
        try {

            this.exceptionLogService.deleteExceptionById(id);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
