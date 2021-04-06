package mmatula.bookingapp.controller;

import mmatula.bookingapp.service.ExceptionLogService;
import mmatula.bookingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final ExceptionLogService exceptionLogService;

    @Autowired
    public UserController(UserService userService, ExceptionLogService exceptionLogService) {
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
    }

    @GetMapping("/api/userId/{email}")
    public Long getUserIdByEmail(@PathVariable String email) {
        try {
            return this.userService.getUserByEmail(email).getId();
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
