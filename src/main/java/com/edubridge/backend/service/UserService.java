package com.edubridge.backend.service;

import com.edubridge.backend.dto.request.CollegeSearchRequest;
import com.edubridge.backend.dto.request.ProfileUpdateRequest;
import com.edubridge.backend.dto.request.RegistrationRequest;
import com.edubridge.backend.exception.BadRequestException;
import com.edubridge.backend.exception.ResourceNotFoundException;
import com.edubridge.backend.model.*;
import com.edubridge.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private CollegeProfileRepository collegeProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Register
    public String registerUser(RegistrationRequest request) {
        // Throw exception instead of returning error message
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        if (request.getRole() == Role.SCHOOL_STUDENT) {
            SchoolStudentProfile profile = new SchoolStudentProfile();
            profile.setUser(savedUser);
            profile.setName(request.getName());
            profile.setSchoolName(request.getSchoolName());
            profile.setClassLevel(request.getClassLevel());
            profile.setInterests(request.getInterests());
            schoolProfileRepository.save(profile);

        } else if (request.getRole() == Role.COLLEGE_STUDENT) {
            CollegeStudentProfile profile = new CollegeStudentProfile();
            profile.setUser(savedUser);
            profile.setCollegeId(request.getCollegeId());
            profile.setName(request.getName());
            profile.setBranch(request.getBranch());
            profile.setYearOfStudy(request.getYearOfStudy());
            profile.setBio(request.getBio());
            profile.setSkills(request.getSkills());
            profile.setAvailabilityStatus(request.getAvailabilityStatus());
            collegeProfileRepository.save(profile);
        }

        return "User registered successfully with ID: " + savedUser.getId();
    }

    // 2. Get all College Student Profiles (For browsing)
    public List<CollegeStudentProfile> getAllCollegeProfiles() {
        return collegeProfileRepository.findAll();
    }

    // 3. Update Profile (Handle both School and College)
    @Transactional
    public void updateUserProfile(String email, ProfileUpdateRequest updateDetails) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        if (user.getRole() == Role.SCHOOL_STUDENT) {
            SchoolStudentProfile profile = schoolProfileRepository.findByUserId(user.getId());
            if (profile == null) {
                throw new ResourceNotFoundException("School profile not found");
            }

            // Only update provided fields (null-safe)
            if (updateDetails.getName() != null) profile.setName(updateDetails.getName());
            if (updateDetails.getSchoolName() != null) profile.setSchoolName(updateDetails.getSchoolName());
            if (updateDetails.getClassLevel() != null) profile.setClassLevel(updateDetails.getClassLevel());
            if (updateDetails.getInterests() != null) profile.setInterests(updateDetails.getInterests());

            schoolProfileRepository.save(profile);

        } else if (user.getRole() == Role.COLLEGE_STUDENT) {
            CollegeStudentProfile profile = collegeProfileRepository.findByUserId(user.getId());
            if (profile == null) {
                throw new ResourceNotFoundException("College profile not found");
            }

            // Only update provided fields (null-safe)
            if (updateDetails.getName() != null) profile.setName(updateDetails.getName());
            if (updateDetails.getBio() != null) profile.setBio(updateDetails.getBio());
            if (updateDetails.getSkills() != null) profile.setSkills(updateDetails.getSkills());
            if (updateDetails.getAvailabilityStatus() != null) profile.setAvailabilityStatus(updateDetails.getAvailabilityStatus());

            collegeProfileRepository.save(profile);
        }
    }


    public List<CollegeStudentProfile> searchCollegeStudents(CollegeSearchRequest request) {
        // Apply filters based on what's provided
        if (request.getCollegeId() != null && request.getBranch() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByCollegeIdAndBranchAndYearOfStudy(
                    request.getCollegeId(), request.getBranch(), request.getYearOfStudy());
        }
        else if (request.getCollegeId() != null && request.getBranch() != null) {
            return collegeProfileRepository.findByCollegeIdAndBranch(
                    request.getCollegeId(), request.getBranch());
        }
        else if (request.getCollegeId() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByCollegeIdAndYearOfStudy(
                    request.getCollegeId(), request.getYearOfStudy());
        }
        else if (request.getBranch() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByBranchAndYearOfStudy(
                    request.getBranch(), request.getYearOfStudy());
        }
        else if (request.getCollegeId() != null) {
            return collegeProfileRepository.findByCollegeId(request.getCollegeId());
        }
        else if (request.getBranch() != null) {
            return collegeProfileRepository.findByBranch(request.getBranch());
        }
        else if (request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByYearOfStudy(request.getYearOfStudy());
        }
        else if (request.getName() != null && !request.getName().trim().isEmpty()) {
            return collegeProfileRepository.findByNameContaining(request.getName());
        }
        else {
            // No filters - return all
            return collegeProfileRepository.findAll();
        }
    }

    public Page<CollegeStudentProfile> searchCollegeStudentsPaginated(CollegeSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Apply filters based on what's provided
        if (request.getCollegeId() != null && request.getBranch() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByCollegeIdAndBranchAndYearOfStudy(
                    request.getCollegeId(), request.getBranch(), request.getYearOfStudy(), pageable);
        }
        else if (request.getCollegeId() != null && request.getBranch() != null) {
            return collegeProfileRepository.findByCollegeIdAndBranch(
                    request.getCollegeId(), request.getBranch(), pageable);
        }
        else if (request.getCollegeId() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByCollegeIdAndYearOfStudy(
                    request.getCollegeId(), request.getYearOfStudy(), pageable);
        }
        else if (request.getBranch() != null && request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByBranchAndYearOfStudy(
                    request.getBranch(), request.getYearOfStudy(), pageable);
        }
        else if (request.getCollegeId() != null) {
            return collegeProfileRepository.findByCollegeId(request.getCollegeId(), pageable);
        }
        else if (request.getBranch() != null) {
            return collegeProfileRepository.findByBranch(request.getBranch(), pageable);
        }
        else if (request.getYearOfStudy() != null) {
            return collegeProfileRepository.findByYearOfStudy(request.getYearOfStudy(), pageable);
        }
        else if (request.getName() != null && !request.getName().trim().isEmpty()) {
            return collegeProfileRepository.findByNameContaining(request.getName(), pageable);
        }
        else {
            // No filters - return all with pagination
            return collegeProfileRepository.findAll(pageable);
        }
    }

    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile image) {
        // Validate image
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        // Validate file type
        String contentType = image.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"))) {
            throw new BadRequestException("Only JPG and PNG images are allowed");
        }

        // Validate file size (max 5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Image size must be less than 5MB");
        }

        try {
            // Convert image to Base64
            byte[] bytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);

            // Create data URI prefix
            String base64Prefix = "data:" + contentType + ";base64,";
            String fullBase64 = base64Prefix + base64Image;

            // Save to user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("User not found"));
            user.setProfileImage(fullBase64);
            userRepository.save(user);

            return "Profile image uploaded successfully";
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    public String getProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return user.getProfileImage();
    }

    public CollegeStudentProfile getCollegeProfile(Long userId) {
        CollegeStudentProfile profile = collegeProfileRepository.findByUserId(userId);
        if(profile == null){
            throw new ResourceNotFoundException("College profile not found for user ID: "+userId);
        }
        return profile;
    }

    // Helper: Get school profile by user ID
    public SchoolStudentProfile getSchoolProfile(Long userId) {
        SchoolStudentProfile profile = schoolProfileRepository.findByUserId(userId);
        if(profile == null){
            throw new ResourceNotFoundException("College profile not found for user ID: "+userId);
        }
        return profile;
    }

    // Helpers
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}