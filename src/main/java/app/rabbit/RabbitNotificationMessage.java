package app.rabbit;

public class RabbitNotificationMessage {

    private final long id;

    public RabbitNotificationMessage(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
