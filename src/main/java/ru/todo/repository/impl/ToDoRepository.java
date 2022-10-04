package ru.todo.repository.impl;

import ru.todo.domain.ToDo;
import ru.todo.repository.CommonRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ToDoRepository implements CommonRepository<ToDo> {

    private Map<String, ToDo> toDos = new HashMap<>();

    @Override
    public ToDo save(ToDo domain) {
        ToDo res = toDos.get(domain.getId());
        if (res != null){
            res.setModified(LocalDateTime.now());
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