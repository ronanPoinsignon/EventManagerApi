package app.web.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WebException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
