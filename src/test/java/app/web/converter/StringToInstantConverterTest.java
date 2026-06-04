package app.web.converter;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StringToInstantConverterTest {

    private final StringToInstantConverter converter = new StringToInstantConverter();

    @Test
    @Order(1)
    void testBasicFormats() {
        // heure hivers
        List<String> stringDateList = new ArrayList<>(List.of(
                "2026-02-01T15:30[Europe/Paris]",
                "2026-02-01T15:30+01:00",
                "2026-02-01T15:30:00+01:00",
                "2026-02-01T13:30-01:00",
                "2026-02-01T15:30[Africa/Algiers]",
                "2026-02-01T15:30[UTC+01:00]",
                "2026-02-01T15:30[GMT+01:00]"
        ));
        for(int i = 1; i < 10; i++) {
            var sb = new StringBuilder("2026-02-01T15:30:00.");
            sb.repeat("0", i);
            stringDateList.add(sb.append("+01:00").toString());
        }
        var date = LocalDate.of(2026, 2, 1);
        var baseValue = LocalDateTime.of(date, LocalTime.of(14, 30)).toInstant(ZoneOffset.UTC);
        for(var stringDate : stringDateList) {
            var result = converter.convert(stringDate);
            Assertions.assertEquals(baseValue, result);
        }

        // heure été
        stringDateList = new ArrayList<>(List.of(
                "2026-07-01T16:30[Europe/Paris]",
                "2026-07-01T15:30+01:00",
                "2026-07-01T15:30:00+01:00",
                "2026-07-01T13:30-01:00",
                "2026-07-01T15:30[Africa/Algiers]",
                "2026-07-01T15:30[UTC+01:00]",
                "2026-07-01T15:30[GMT+01:00]"
        ));
        for(int i = 1; i < 10; i++) {
            var sb = new StringBuilder("2026-07-01T15:30:00.");
            sb.repeat("0", i);
            stringDateList.add(sb.append("+01:00").toString());
        }
        date = LocalDate.of(2026, 7, 1);
        baseValue = LocalDateTime.of(date, LocalTime.of(14, 30)).toInstant(ZoneOffset.UTC);
        for(var stringDate : stringDateList) {
            var result = converter.convert(stringDate);
            Assertions.assertEquals(baseValue, result);
        }
    }

}
