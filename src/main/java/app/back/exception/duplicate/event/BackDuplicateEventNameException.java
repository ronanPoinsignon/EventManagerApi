package app.back.exception.duplicate.event;

import app.back.exception.duplicate.BackDuplicateConstraintException;

public class BackDuplicateEventNameException extends BackDuplicateConstraintException {

    public BackDuplicateEventNameException() {
        super("Un événement de ce nom existe déjà.");
    }

}
