package app.web.converter;

import app.web.exception.BadRequestException;
import org.jspecify.annotations.NullMarked;

import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class StringToLocalDateTimeConverter implements StringConverter<LocalDateTime> {

    private static final String[] LOCAL8DATE_TIME_STRING_FORMAT = new String[] {
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "dd/MM/yyyy"
    };

    private List<DateTimeFormatter> computeParsers() {
        List<DateTimeFormatter> formatters = new ArrayList<>(LOCAL8DATE_TIME_STRING_FORMAT.length * 2 + 1);
        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        for(var format : LOCAL8DATE_TIME_STRING_FORMAT) {
            var basicFormat = DateTimeFormatter.ofPattern(format);
            var pattern = new DateTimeFormatterBuilder().parseCaseInsensitive()
                    .append(basicFormat)
                    .appendLiteral('T')
                    .append(DateTimeFormatter.ISO_LOCAL_TIME);
            formatters.add(pattern.toFormatter());
            formatters.add(DateTimeFormatter.ofPattern(format));
        }

        return formatters;
    }

    @Override
    @NullMarked
    public LocalDateTime convert(String source) {
        if(source.isBlank()) {
            return null;
        }

        for(var pattern : computeParsers()) {
            var date = checkIfPatternMatches(source, pattern);
            if(date == null) {
                continue;
            }

            return parse(date);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun format de date correspondant.");
    }

    private TemporalAccessor checkIfPatternMatches(String value, DateTimeFormatter pattern) {
        var startPosition = 0;
        var pos = new ParsePosition(startPosition);
        var result = pattern.parseUnresolved(value, pos);
        if (pos.getIndex() == startPosition || pos.getIndex() < value.length()) {
            return null;
        }

        return result;
    }

    private LocalDateTime parse(TemporalAccessor temporalAccessor) {
        var milli = getTemporalAccessor(temporalAccessor, ChronoField.MILLI_OF_SECOND);
        var second = getTemporalAccessor(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        var minute = getTemporalAccessor(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        var hour = getTemporalAccessor(temporalAccessor, ChronoField.HOUR_OF_DAY);
        var day = getTemporalAccessor(temporalAccessor, ChronoField.DAY_OF_MONTH);
        var month = getTemporalAccessor(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        var year = getTemporalAccessor(temporalAccessor, ChronoField.YEAR);
        if(year == 0) {
            year = getTemporalAccessor(temporalAccessor, ChronoField.YEAR_OF_ERA);
        }

        try {
            return LocalDateTime.of(year, month, day, hour, minute, second, milli);
        } catch(DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date n'est pas valide.");
        }
    }

    private int getTemporalAccessor(TemporalAccessor temporalAccessor, ChronoField chronoField) {
        try {
            return temporalAccessor.isSupported(chronoField) ? temporalAccessor.get(chronoField) : 0;
        } catch(DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date n'est pas valide.");
        }
    }

}
