package com.edubridge.backend.repository;

import com.edubridge.backend.model.CollegeStudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeProfileRepository extends JpaRepository<CollegeStudentProfile, Long> {

    CollegeStudentProfile findByUserId(Long userId);

    // WITHOUT PAGINATION (for backward compatibility)
    List<CollegeStudentProfile> findByCollegeId(Long collegeId);
    List<CollegeStudentProfile> findByBranch(String branch);
    List<CollegeStudentProfile> findByYearOfStudy(String yearOfStudy);
    List<CollegeStudentProfile> findByCollegeIdAndBranch(Long collegeId, String branch);
    List<CollegeStudentProfile> findByCollegeIdAndYearOfStudy(Long collegeId, String yearOfStudy);
    List<CollegeStudentProfile> findByBranchAndYearOfStudy(String branch, String yearOfStudy);
    List<CollegeStudentProfile> findByCollegeIdAndBranchAndYearOfStudy(
            Long collegeId, String branch, String yearOfStudy);

    @Query("SELECT c FROM CollegeStudentProfile c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CollegeStudentProfile> findByNameContaining(@Param("name") String name);

    //WITH PAGINATION (new methods)
    Page<CollegeStudentProfile> findByCollegeId(Long collegeId, Pageable pageable);
    Page<CollegeStudentProfile> findByBranch(String branch, Pageable pageable);
    Page<CollegeStudentProfile> findByYearOfStudy(String yearOfStudy, Pageable pageable);
    Page<CollegeStudentProfile> findByCollegeIdAndBranch(Long collegeId, String branch, Pageable pageable);
    Page<CollegeStudentProfile> findByCollegeIdAndYearOfStudy(Long collegeId, String yearOfStudy, Pageable pageable);
    Page<CollegeStudentProfile> findByBranchAndYearOfStudy(String branch, String yearOfStudy, Pageable pageable);
    Page<CollegeStudentProfile> findByCollegeIdAndBranchAndYearOfStudy(
            Long collegeId, String branch, String yearOfStudy, Pageable pageable);

    @Query("SELECT c FROM CollegeStudentProfile c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CollegeStudentProfile> findByNameContaining(@Param("name") String name, Pageable pageable);
}