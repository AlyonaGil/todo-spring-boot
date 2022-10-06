package ru.todo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.todo.domain.ToDo;

public interface ToDoRepository extends CrudRepository<ToDo, String> {
}
