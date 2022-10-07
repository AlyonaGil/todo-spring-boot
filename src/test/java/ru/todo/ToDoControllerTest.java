package ru.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.todo.domain.ToDo;
import ru.todo.repository.ToDoRepository;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ToDoControllerTest {
    @Autowired
    private ObjectMapper objectMapper; //класс преобразовывает объект в JSON-строку
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ToDoRepository repository;

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }

    private ToDo createTestToDo(String description) {
        ToDo todo = new ToDo(description);
        return repository.save(todo);
    }

    @Test
    public void givenToDo_whenAdd_thenStatus201andPersonReturned() throws Exception {
        ToDo todo = createTestToDo("Test todo");

        mockMvc.perform(
                post("/api/todo")
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test todo"));
    }

    @Test
    public void givenId_whenGetExistingToDoById_thenStatus200andToDoReturned() throws Exception{
        String id = createTestToDo("Test todo").getId();
        mockMvc.perform(
                get("/api/todo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test todo"))
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    public void givenToDos_whenGetToDos_thenStatus200andToDoReturned() throws Exception{
        ToDo todo1 = createTestToDo("Test todo 1");
        ToDo todo2 = createTestToDo("Test todo 2");
        mockMvc.perform(
                get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(todo1, todo2))));
    }

    @Test
    public void givenId_whenGetNotExistingToDo_thenStatus404() throws Exception{
        mockMvc.perform(
                get("/api/todo/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenToDo_whenCompleted_thenStatus200andUpdatedReturns() throws Exception{
        ToDo todo = createTestToDo("Test todo");
        String id = todo.getId();
        mockMvc.perform(
                patch("/api/todo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.description").value("Test todo"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    public void givenId_whenDeleteToDoById_thenStatus204() throws Exception{
        String id = createTestToDo("Test todo").getId();
        mockMvc.perform(
                delete("/api/todo/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenToDo_whenDeleteToDo_thenStatus204() throws Exception{
        ToDo todo = createTestToDo("Test todo");
        mockMvc.perform(
                delete("/api/todo")
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

}
