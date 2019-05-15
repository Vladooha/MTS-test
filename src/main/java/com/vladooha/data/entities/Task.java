package com.vladooha.data.entities;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="task")
@Data
public class Task {
    @Id
    @Type(type="uuid-char")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String timestamp;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
    }
}
