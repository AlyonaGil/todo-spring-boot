package ru.todo.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ToDo {
    @NotNull
    private String id;
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created;
    private LocalDateTime modified;
    private boolean completed;

    public ToDo(){
        id = UUID.randomUUID().toString();
        LocalDateTime date = LocalDateTime.now();
        created = date;
        modified = date;
    }

    public ToDo(String description) {
        this();
        this.description = description;
    }
}
