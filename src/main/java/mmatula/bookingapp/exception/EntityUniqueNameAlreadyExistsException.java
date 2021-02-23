package mmatula.bookingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.NamingException;

public class EntityUniqueNameAlreadyExistsException extends NamingException {

    public EntityUniqueNameAlreadyExistsException() {
        super();
    }
}
