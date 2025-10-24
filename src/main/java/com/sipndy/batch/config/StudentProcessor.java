package com.sipndy.batch.config;

import com.sipndy.batch.student.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student,Student> {
    @Override
    public Student process(Student student) throws Exception {
        student.setId(null);
        return student;
    }
}
