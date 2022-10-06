package ru.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.todo.domain.ToDo;
import ru.todo.domain.ToDoBuilder;
import ru.todo.repository.ToDoRepository;
import ru.todo.validation.ToDoValidationError;
import ru.todo.validation.ToDoValidationErrorBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ToDoController {
    private ToDoRepository toDoRepository;

    @Autowired
    public ToDoController(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @GetMapping("/todo")
    public ResponseEntity<Iterable<ToDo>> getToDos(){
        return ResponseEntity.ok(toDoRepository.findAll());
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable String id){
        Optional<ToDo> toDo = toDoRepository.findById(id);
        if(toDo.isPresent())
            return ResponseEntity.ok(toDo.get());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/todo")
    public ResponseEntity<ToDo> deleteToDo(@RequestBody ToDo todo){
        toDoRepository.delete(todo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("todo/{id}")
    public ResponseEntity<ToDo> deleteToDo(@PathVariable String id){
        toDoRepository.delete(ToDoBuilder.create().withId(id).build());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/todo/{id}")
    public ResponseEntity<ToDo> setCompleted(@PathVariable String id){
        Optional<ToDo> toDo = toDoRepository.findById(id);
        if (!toDo.isPresent()){
            return ResponseEntity.notFound().build();
        }
        ToDo res = toDo.get();
        res.setCompleted(true);
        toDoRepository.save(res);

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
        ToDo res = toDoRepository.save(todo);
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
