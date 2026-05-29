package app.web.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends WebException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

}
