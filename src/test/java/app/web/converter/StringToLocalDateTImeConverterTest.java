package app.web.converter;

import app.web.exception.BadRequestException;
import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class StringToLocalDateTImeConverterTest {

    private final StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();

    @Test
    @Order(1)
    void testBasicFormats() {
        var stringDateList = List.of(
                "01-02-2020",
                "01/02/2020",
                "2020-02-01",
                "2020/02/01"
        );
        var date = LocalDate.of(2020, 2, 1);
        for(var stringDate : stringDateList) {
            var result = converter.convert(stringDate);
            Assertions.assertEquals(LocalDateTime.of(date, LocalTime.of(0, 0)), result);
        }

        stringDateList = List.of(
                "01-01-2000",
                "01/01/2000",
                "2000-01-01",
                "2000/01/01"
        );
        date = LocalDate.of(2000, 1, 1);
        for(var stringDate : stringDateList) {
            var result = converter.convert(stringDate);
            Assertions.assertEquals(LocalDateTime.of(date, LocalTime.of(0, 0)), result);
        }

        stringDateList = List.of(
                "31-12-1999",
                "31/12/1999",
                "1999-12-31",
                "1999/12/31"
        );
        date = LocalDate.of(1999, 12, 31);
        for(var stringDate : stringDateList) {
            var result = converter.convert(stringDate);
            Assertions.assertEquals(LocalDateTime.of(date, LocalTime.of(0, 0)), result);
        }

        var result = converter.convert("29-02-2004");
        Assertions.assertEquals(LocalDateTime.of(2004, 2, 29, 0, 0), result);

        result = converter.convert("01-01-2000");
        Assertions.assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), result);
        result = converter.convert("01/01/2000");
        Assertions.assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), result);
        result = converter.convert("01/01/2000");
        Assertions.assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), result);
    }

    @Test
    void test() {
        converter.convert("2026-12-31");
    }

    @Test
    @Order(2)
    void testComplexFormat() {
        var stringDateTimeList = List.of(
                "01-02-2020T01:02:03",
                "01/02/2020T01:02:03",
                "2020-02-01T01:02:03",
                "2020/02/01T01:02:03"
        );
        var stringDateTimeListNoSeconds = List.of(
                "01-02-2020T01:02",
                "01/02/2020T01:02",
                "2020-02-01T01:02",
                "2020/02/01T01:02"
        );

        for(var stringDateTime : stringDateTimeList) {
            var result = converter.convert(stringDateTime);
            Assertions.assertEquals(LocalDateTime.of(2020, 2, 1, 1, 2, 3), result);
        }
        for(var stringDateTime : stringDateTimeListNoSeconds) {
            var result = converter.convert(stringDateTime);
            Assertions.assertEquals(LocalDateTime.of(2020, 2, 1, 1, 2, 0), result);
        }
    }

    @Test
    @Order(3)
    void testIncoherentFormat() {
        var stringIncoherentDate = List.of(
                "",
                "01-02-202",
                "01-13-2020",
                "32-02-2020",
                "29-02-2100",
                "01-02-2020 01:02:03"
        );

        for(var incoherentDate : stringIncoherentDate) {
            Assertions.assertThrows(BadRequestException.class, () -> converter.convert(incoherentDate));
        }
    }

}
