package app.web.converter;

import app.web.exception.BadRequestException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.regex.Pattern;

public class StringToInstantConverter implements StringConverter<Instant> {

    private static final StringToLocalDateTimeConverter localDateTimeConverter = new StringToLocalDateTimeConverter();

    @Override
    public Instant convert(String source) {
        var regex = "T\\d{2}:\\d{2}(:\\d{2}(\\.\\d{1,9})?)?";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(source);

        if(!matcher.find()) {
            throw new BadRequestException("Le format de la date n'est pas correct.");
        }

        var dateOffsetValue = matcher.toMatchResult().group();
        var split = source.split(regex);

        if(split.length != 2) {
            throw new BadRequestException("Le format de la date n'est pas un format ISO.");
        }

        var date = split[0] + dateOffsetValue;
        var offset = split[1];

        if(offset.isBlank()) {
            throw new BadRequestException("Le format de l'offset est obligatoire.");
        }

        var localDateTime = localDateTimeConverter.convert(date);

        if(offset.equals("Z")) {
            return localDateTime.toInstant(ZoneOffset.UTC);
        }

        if(offset.startsWith("[")) {
            offset = offset.substring(1, offset.length() - 1);
        }
        return localDateTime.atZone(ZoneId.of(offset)).toInstant();
    }

}
