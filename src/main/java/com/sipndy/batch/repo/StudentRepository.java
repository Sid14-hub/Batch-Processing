package com.sipndy.batch.repo;

import com.sipndy.batch.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
}
