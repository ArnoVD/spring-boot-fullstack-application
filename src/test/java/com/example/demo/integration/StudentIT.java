package com.example.demo.integration;

import com.example.demo.student.Gender;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@student.edu", StringUtils.trimAllWhitespace(name).toLowerCase());

        Student student = new Student (
                name,
                email,
                Gender.MALE
        );

        // when
        // Here we are testing the PostMapping
        ResultActions resultActions = mockMvc.perform(post("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(student)));

        // then
        // We assert that the server response code is a 200
        resultActions.andExpect(status().isOk());
        // Check if student is in the db
        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingElementComparatorIgnoringFields("id")
                .contains(student);
    }


    @Test
    void canDeleteStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@amigoscode.edu",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Student student = new Student(
                name,
                email,
                Gender.FEMALE
        );

        // Add the student
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        // Get the students data as an MvcResult (andReturn())
        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Grab the response as a string
        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        // Map the response string as a list of student (objects)
        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {}
        );

        // Find the correct student to grab the id
        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + email + " not found"));

        // when
        // Perform the delete of the student
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }
}
