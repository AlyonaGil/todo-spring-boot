package ru.todo.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class ToDo {
    @NotNull
    private String id;
    @NotNull
    @NotBlank
    private String description;
    private Date created;
    private Date modified;
    private boolean completed;

    public ToDo(){
        id = UUID.randomUUID().toString();
        Date date = new Date();
        created = date;
        modified = date;
    }

    public ToDo(String description) {
        this();
        this.description = description;
    }
}
