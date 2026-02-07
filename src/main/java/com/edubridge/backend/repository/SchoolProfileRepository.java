package com.edubridge.backend.repository;

import com.edubridge.backend.model.SchoolStudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolProfileRepository extends JpaRepository<SchoolStudentProfile, Long> {
    SchoolStudentProfile findByUserId(Long userId);
}