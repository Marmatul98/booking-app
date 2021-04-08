package mmatula.bookingapp.controller;

import mmatula.bookingapp.dto.UserDTO;
import mmatula.bookingapp.request.PasswordResetBody;
import mmatula.bookingapp.security.JwtUtil;
import mmatula.bookingapp.security.dto.AuthenticationRequest;
import mmatula.bookingapp.security.dto.AuthenticationResponse;
import mmatula.bookingapp.service.AuthenticationService;
import mmatula.bookingapp.service.ExceptionLogService;
import mmatula.bookingapp.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final ExceptionLogService exceptionLogService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil, AuthenticationService authenticationService, ExceptionLogService exceptionLogService) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
        this.exceptionLogService = exceptionLogService;
    }

    @PostMapping("/register")
    public void register(@RequestBody UserDTO userDTO) {
        try {
            authenticationService.register(userDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/requestPasswordReset")
    public void requestPasswordReset(@RequestBody String email) {
        try {
            authenticationService.requestPasswordReset(email);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resetPassword")
    public void resetPassword(@RequestBody PasswordResetBody passwordResetBody) {
        try {
            authenticationService.resetPassword(passwordResetBody);
        } catch (Exception e) {
            this.exceptionLogService.addException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
