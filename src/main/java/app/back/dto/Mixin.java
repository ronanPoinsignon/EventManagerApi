package app.back.dto;

public interface Mixin<T extends AbstractEntity> {

    void write(T entity);

}
