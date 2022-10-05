package ru.todo.repository.impl;

import org.springframework.stereotype.Repository;
import ru.todo.domain.ToDo;
import ru.todo.repository.CommonRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ToDoRepository implements CommonRepository<ToDo> {

    private final Map<String, ToDo> toDos = new HashMap<>();

    @Override
    public ToDo save(ToDo domain) {
        ToDo res = toDos.get(domain.getId());
        if (res != null){
            res.setModified(new Date());
            res.setDescription(domain.getDescription());
            res.setCompleted(domain.isCompleted());
            domain = res;
        }
        toDos.put(domain.getId(), domain);
        return toDos.get(domain.getId());
    }

    @Override
    public Iterable<ToDo> save(Collection<ToDo> domains) {
        domains.forEach(this::save);
        return findAll();
    }

    @Override
    public void delete(ToDo domain) {
        toDos.remove(domain.getId());
    }

    @Override
    public ToDo findById(String id) {
        return toDos.get(id);
    }

    @Override
    public Iterable<ToDo> findAll() {
        return toDos
                .entrySet()
                .stream()
                .sorted(Comparator.comparing((Map.Entry<String, ToDo> a) -> a.getValue().getCreated()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}