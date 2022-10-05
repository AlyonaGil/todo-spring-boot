package ru.todo.repository.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.todo.domain.ToDo;
import ru.todo.repository.CommonRepository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ToDoRepository implements CommonRepository<ToDo> {

    private static final String SQL_INSERT = "insert into todo (id, " +
            "description, created, modified, completed) values (:id,:description," +
            ":created,:modified,:completed)";
    private static final String SQL_QUERY_FIND_ALL = "select id, description, created, " +
            "modified, completed from todo";
    private static final String SQL_QUERY_FIND_BY_ID = SQL_QUERY_FIND_ALL + " where id = :id";
    private static final String SQL_UPDATE = "update todo set description = " +
            ":description, modified = :modified, completed = :completed where id = :id";
    private static final String SQL_DELETE = "delete from todo where id = :id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ToDoRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<ToDo> toDoRowMapper = (ResultSet rs, int rowNum) ->{
        ToDo todo = new ToDo();
        todo.setId(rs.getString("id"));
        todo.setDescription(rs.getString("description"));
        todo.setModified(rs.getTimestamp("modified").toLocalDateTime());
        todo.setCreated(rs.getTimestamp("created").toLocalDateTime());
        todo.setCompleted(rs.getBoolean("completed"));
        return todo;
    };

    @Override
    public ToDo save(final ToDo domain) {
        ToDo res = findById(domain.getId());
        if (res != null){
            res.setModified(LocalDateTime.now());
            res.setDescription(domain.getDescription());
            res.setCompleted(domain.isCompleted());
            return upsert(res, SQL_UPDATE);
        }
        return upsert(domain, SQL_INSERT);
    }

    private ToDo upsert(final ToDo todo, String sql){
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id",todo.getId());
        namedParameters.put("description",todo.getDescription());
        namedParameters.put("created",java.sql.Timestamp.valueOf(todo.getCreated()));
        namedParameters.put("modified",java.sql.Timestamp.valueOf(todo.getModified()));
        namedParameters.put("completed",todo.isCompleted());
        this.jdbcTemplate.update(sql,namedParameters);
        return findById(todo.getId());
    }

    @Override
    public Iterable<ToDo> save(Collection<ToDo> domains) {
        domains.forEach(this::save);
        return findAll();
    }

    @Override
    public void delete(final ToDo domain) {
        Map<String, String> namedParameters = Collections.singletonMap("id", domain.getId());
        this.jdbcTemplate.update(SQL_DELETE,namedParameters);
    }

    @Override
    public ToDo findById(String id) {
        try {
            Map<String, String> namedParameters = Collections.singletonMap("id", id);
            return this.jdbcTemplate.queryForObject(
                    SQL_QUERY_FIND_BY_ID,
                    namedParameters,
                    toDoRowMapper);
        }catch (EmptyResultDataAccessException ex){
            return null;
        }
    }

    @Override
    public Iterable<ToDo> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL, toDoRowMapper);
    }
}