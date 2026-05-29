package app.back.exception.duplicate;

public class BackUniqueEntityAttribute extends BackConstraintException {

    public BackUniqueEntityAttribute(String attributName) {
        super("L'attribut " + attributName + " doit être unique et non null.");
    }

}
