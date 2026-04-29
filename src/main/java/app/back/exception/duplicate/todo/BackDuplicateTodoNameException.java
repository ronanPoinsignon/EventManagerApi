package app.back.exception.duplicate.todo;

import app.back.exception.duplicate.BackDuplicateConstraintException;

public class BackDuplicateTodoNameException extends BackDuplicateConstraintException {

    public BackDuplicateTodoNameException() {
        super("Le nom du todo est déjà existant pour cet événement.");
    }

}
