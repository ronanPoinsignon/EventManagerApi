package app.back.exception;

public abstract class BackException extends RuntimeException {

    public BackException(String message) {
        super(message);
    }

}
