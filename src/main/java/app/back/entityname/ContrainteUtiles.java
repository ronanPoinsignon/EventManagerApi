package app.back.entityname;

import app.back.exception.duplicate.BackConstraintException;
import app.back.exception.duplicate.event.BackDuplicateEventNameException;
import app.back.exception.duplicate.event.BackDuplicateEventParticipant;
import app.back.exception.duplicate.todo.BackDuplicateTodoNameException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ContrainteUtiles {

    public static final Map<String, Map<String, Supplier<? extends BackConstraintException>>> CONTRAINTE_EXCEPTION_MAP = new HashMap<>();

    static {
        init();
    }

    private static void init() {

        Map<String, Supplier<? extends BackConstraintException>> eventMap = new HashMap<>();
        eventMap.put(Contrainte.EVENT_DUPLICATE_NAME, BackDuplicateEventNameException::new);
        eventMap.put(Contrainte.EVENT_DUPLICATE_PARTICIPANT, BackDuplicateEventParticipant::new);

        Map<String, Supplier<? extends BackConstraintException>> todoMap = new HashMap<>();
        todoMap.put(Contrainte.TODO_DUPLICATE_NAME, BackDuplicateTodoNameException::new);

        Map<String, Supplier<? extends BackConstraintException>> discordMemberMap = new HashMap<>();

        CONTRAINTE_EXCEPTION_MAP.put(EntityTable.EVENT, Collections.unmodifiableMap(eventMap));
        CONTRAINTE_EXCEPTION_MAP.put(EntityTable.DISCORD_MEMBER, Collections.unmodifiableMap(discordMemberMap));
        CONTRAINTE_EXCEPTION_MAP.put(EntityTable.TODO_ENTRY, Collections.unmodifiableMap(todoMap));
    }

}
