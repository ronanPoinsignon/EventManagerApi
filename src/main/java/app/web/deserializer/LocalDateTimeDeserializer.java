package app.web.deserializer;

import app.web.converter.StringToLocalDateTimeConverter;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalDateTime;

public class LocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {

    private static final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();

    @Override
    public LocalDateTime deserialize(tools.jackson.core.JsonParser p, tools.jackson.databind.DeserializationContext ctxt) throws tools.jackson.core.JacksonException {
        var stringDate = p.getValueAsString();
        return converter.convert(stringDate);
    }

}
