package app.back.entityname;

import java.util.List;

public interface Contrainte {

    String EVENT_DUPLICATE_NAME = "duplicate_event_name";
    String EVENT_DUPLICATE_PARTICIPANT = "duplicate_event_participant";
    String TODO_DUPLICATE_NAME = "duplicate_todo_name";

    String EVENT_OWNER = "owner";
    String EVENT_NAME = "event_name";
    String EVENT_CREATION_DATE = "creation_date";
    List<String> EVENT_UNIQUE_ATTRIBUTES = List.of(EVENT_OWNER, EVENT_NAME, EVENT_CREATION_DATE);

    String TODO_NAME = "name";
    String TODO_VALUE = "todo";
    String TODO_EVENT_ID = "event_id";
    List<String> TODO_UNIQUE_ATTRIBUTES = List.of(TODO_NAME, TODO_VALUE, TODO_EVENT_ID);

    String USER_ATTRIBUTES_DISCORD_ID = "discord_id";
    String USER_ATTRIBUTE_KEYCLOAK_USER_ID = "keycloak_user_id";
    List<String> USER_ATTRIBUTES_UNIQUE_ATTRIBUTES = List.of(USER_ATTRIBUTES_DISCORD_ID, USER_ATTRIBUTE_KEYCLOAK_USER_ID);

    String NOTIFICATION_EXECUTION_DATE = "execution_date";
    String NOTIFICATION_ENTITY_TYPE = "entity_type";

    String DISCORD_GUILD_ID = "discord_guild_id";

}
