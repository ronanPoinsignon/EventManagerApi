package app.back.exception.duplicate;

import app.back.exception.BackBadRequestException;

public class BackConstraintException extends BackBadRequestException {

    public BackConstraintException(String message) {
        super(message);
    }

}
