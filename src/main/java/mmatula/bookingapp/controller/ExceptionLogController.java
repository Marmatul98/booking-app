package mmatula.bookingapp.controller;

import mmatula.bookingapp.model.ExceptionLog;
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

    @Autowired
    public ExceptionLogController(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }

    @GetMapping("/mock")
    public void mockException() {
        try {
            throw new NullPointerException("adasd");
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exceptionLog")
    public List<ExceptionLog> getAllExceptionLogs() {
        try {
            return this.exceptionLogService.getAllExceptionLogs();
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
