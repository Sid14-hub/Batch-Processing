package com.sipndy.batch.config;

import com.sipndy.batch.student.Student;
import org.springframework.batch.item.ItemProcessor;
//this is the processor call which implements ItemProcessor<Input,Output> this is responsible
//for manipulating the data and return the data used in processor stpe
public class StudentProcessor implements ItemProcessor<Student,Student> {
    @Override
    public Student process(Student student) throws Exception {
        student.setId(null);
        return student;
    }
}
