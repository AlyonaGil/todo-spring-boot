package ru.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.todo.domain.ToDo;
import ru.todo.domain.ToDoBuilder;
import ru.todo.repository.CommonRepository;
import ru.todo.validation.ToDoValidationError;
import ru.todo.validation.ToDoValidationErrorBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class ToDoController {
    private final CommonRepository<ToDo> repository;

    @Autowired
    public ToDoController(CommonRepository<ToDo> repository) {
        this.repository = repository;
    }

    @GetMapping("/todo")
    public ResponseEntity<Iterable<ToDo>> getToDos(){
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable String id){
        return ResponseEntity.ok(repository.findById(id));
    }

    @DeleteMapping("/todo")
    public ResponseEntity<ToDo> deleteToDo(@RequestBody ToDo todo){
        repository.delete(todo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("todo/{id}")
    public ResponseEntity<ToDo> deleteToDo(@PathVariable String id){
        repository.delete(ToDoBuilder.create().withId(id).build());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/todo/{id}")
    public ResponseEntity<ToDo> setCompleted(@PathVariable String id){
        ToDo res = repository.findById(id);
        res.setCompleted(true);
        repository.save(res);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(res.getId())
                .toUri();

        return ResponseEntity.ok().header("Location", location.toString()).build();
    }

    @RequestMapping(value="/todo", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> createToDo(@Valid @RequestBody ToDo todo, Errors errors){
        if (errors.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(ToDoValidationErrorBuilder.fromBindingErrors(errors));
        }
        ToDo res = repository.save(todo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(res.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ToDoValidationError handleException(Exception exception){
        return new ToDoValidationError(exception.getMessage());
    }
}
