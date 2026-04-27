package app.back.dto;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity implements Identifiable {

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

}

