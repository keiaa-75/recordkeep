package com.shing.recordkeep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shing.recordkeep.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findBySectionId(Long sectionId);
}
