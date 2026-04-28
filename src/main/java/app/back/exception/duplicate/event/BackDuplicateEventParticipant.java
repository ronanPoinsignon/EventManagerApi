package app.back.exception.duplicate.event;

import app.back.exception.duplicate.BackDuplicateConstraintException;

public class BackDuplicateEventParticipant extends BackDuplicateConstraintException {

    public BackDuplicateEventParticipant() {
        super("Cette personne est déjà dans la liste des participants.");
    }

}
