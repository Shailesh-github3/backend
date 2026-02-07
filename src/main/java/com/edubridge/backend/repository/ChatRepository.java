package com.edubridge.backend.repository;

import com.edubridge.backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.schoolStudent.id = :schoolId AND c.collegeStudent.id = :collegeId")
    Optional<Chat> findBySchoolStudentIdAndCollegeStudentId(Long schoolId, Long collegeId);

    // Also support reverse lookup (in case we swap the order)
    @Query("SELECT c FROM Chat c WHERE c.schoolStudent.id = :collegeId AND c.collegeStudent.id = :schoolId")
    Optional<Chat> findByCollegeStudentIdAndSchoolStudentId(Long collegeId, Long schoolId);
}