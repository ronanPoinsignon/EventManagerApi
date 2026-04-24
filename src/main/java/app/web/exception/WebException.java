package app.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class WebException extends ResponseStatusException {

    public WebException(HttpStatus status, String message) {
        super(status, message);
    }

}
