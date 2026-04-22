package app.web.converter;

import org.springframework.core.convert.converter.Converter;

@FunctionalInterface
public interface StringConverter<T> extends Converter<String, T> {
}
