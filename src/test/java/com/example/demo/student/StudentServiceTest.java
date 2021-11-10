package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    // Make a mock of the student repo (since it is using the JPA repo and we know that has been tested)
    @Mock private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        // Create a new instance before each test
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();
        // then
        // Verify that the findAll() method has been used by getAllStudents() above
        verify(studentRepository).findAll();
    }

    @Test
    void canGetStudentsById() {
        // given
        Student student = new Student (
                "Arno",
                "arno@student.com",
                Gender.MALE
        );
        // when
        underTest.findStudentById(student.getId());
        // then
        // Verify that the findAll() method has been used by getAllStudents() above
        verify(studentRepository).findById(student.getId());
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student (
                "Arno",
                "arno@student.com",
                Gender.MALE
        );
        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        // Verify that the save method was used in the repo (by addStudent above)
        verify(studentRepository).save(studentArgumentCaptor.capture());

        // Get the captured value
        Student capturedStudent = studentArgumentCaptor.getValue();

        // Check if the captured student is the same as above
        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student (
                "Arno",
                "arno@student.com",
                Gender.MALE
        );

        // This makes the boolean in the selectExistsEmail() method to be true (So that the throw is triggered)
        given(studentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        // Verify that the selectExistsEmail() method never saves a student when an exception is thrown
        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }

}