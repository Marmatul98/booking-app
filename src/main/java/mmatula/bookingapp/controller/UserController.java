package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.dto.mapper.UserModelMapper;
import mmatula.bookingapp.service.ExceptionLogService;
import mmatula.bookingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final ExceptionLogService exceptionLogService;
    private final UserModelMapper userModelMapper;

    @Autowired
    public UserController(UserService userService, ExceptionLogService exceptionLogService, UserModelMapper userModelMapper) {
        this.userService = userService;
        this.exceptionLogService = exceptionLogService;
        this.userModelMapper = userModelMapper;
    }

    @GetMapping("/admin/user")
    public List<UserDTO> getAllUsers() {
        try {
            return this.userModelMapper.entityListToDtoList(this.userService.getAllUsers());
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/user/{email}")
    public UserDTO getUser(@PathVariable String email) {
        try {
            return this.userModelMapper.entityToDto(this.userService.getUserByEmail(email));
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/api/user")
    public void updateUser(@RequestBody UserDTO userDTO) {
        try {
            this.userService.updateUser(userDTO);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
