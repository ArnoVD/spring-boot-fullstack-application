package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// This class will handle the business logic
@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents(){
        // This gives us access to prebuild JPA methods.
        return studentRepository.findAll();
    }

    public Optional<Student> findStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }

    public void addStudent(Student student) {
        // Check if email exists
        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + student.getEmail() + " taken");
        } else {
            studentRepository.save(student);
        }
    }

    public void deleteStudent(Long studentId) {
        // Check if student exists
        if (findStudentById(studentId).isPresent()) {
            studentRepository.deleteById(studentId);
        } else {
            throw new StudentNotFoundException("Student with id " + studentId + " does not exists");
        }

    }
}
