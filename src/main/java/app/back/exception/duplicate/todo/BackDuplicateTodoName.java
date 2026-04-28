package app.back.exception.duplicate.todo;

import app.back.exception.duplicate.BackDuplicateConstraintException;

public class BackDuplicateTodoName extends BackDuplicateConstraintException {

    public BackDuplicateTodoName() {
        super("Le nom du todo est déjà existant pour cet événement.");
    }

}
