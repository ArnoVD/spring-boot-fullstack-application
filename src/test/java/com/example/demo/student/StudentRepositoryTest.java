package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        // After each test delete all the students
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfStudentEmailExists() {
        // given
        String email = "arno@student.com";
        Student student = new Student (
                "Arno",
                email,
                Gender.MALE
        );
        underTest.save(student);

        // when
        Boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldCheckIfStudentEmailDoesNotExists() {
        // given
        String email = "arno@student.com";

        // when
        Boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isFalse();
    }
}