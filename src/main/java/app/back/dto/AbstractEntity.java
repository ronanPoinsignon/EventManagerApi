package app.back.dto;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity<T extends AbstractEntity<T>> implements Identifiable, Mixin<T> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void write(T dbEntity) {
        if(this.id != null) {
            dbEntity.setId(id);
        }
    }
}

