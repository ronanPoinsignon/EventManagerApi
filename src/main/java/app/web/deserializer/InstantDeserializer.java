package app.web.deserializer;

import app.web.converter.StringToInstantConverter;
import tools.jackson.databind.ValueDeserializer;

import java.time.Instant;

public class InstantDeserializer extends ValueDeserializer<Instant> {

    private static final StringToInstantConverter converter = new StringToInstantConverter();

    @Override
    public Instant deserialize(tools.jackson.core.JsonParser p, tools.jackson.databind.DeserializationContext ctxt) throws tools.jackson.core.JacksonException {
        var stringDate = p.getValueAsString();
        return converter.convert(stringDate);
    }

}
