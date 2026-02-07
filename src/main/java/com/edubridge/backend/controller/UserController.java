package com.edubridge.backend.controller;

import com.edubridge.backend.dto.request.CollegeSearchRequest;
import com.edubridge.backend.dto.request.ProfileUpdateRequest;
import com.edubridge.backend.dto.request.RegistrationRequest;
import com.edubridge.backend.dto.response.CollegeStudentResponse;
import com.edubridge.backend.model.CollegeStudentProfile;
import com.edubridge.backend.model.User;
import com.edubridge.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    // POST /register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
        // ✅ No try-catch needed - GlobalExceptionHandler will handle exceptions
        String result = userService.registerUser(request);
        return ResponseEntity.ok(result);
    }

    // GET /college-students (List of seniors to browse)
    @GetMapping("/college-students")
    public ResponseEntity<?> getAllCollegeStudents() {
        List<CollegeStudentProfile> students = userService.getAllCollegeProfiles();
        List<CollegeStudentResponse> response = students.stream()
                .map(CollegeStudentResponse::fromProfile)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/college-students/search")
    public ResponseEntity<?> searchCollegeStudents(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String yearOfStudy,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var request = new CollegeSearchRequest();
        request.setCollegeId(collegeId);
        request.setBranch(branch);
        request.setYearOfStudy(yearOfStudy);
        request.setName(name);
        request.setPage(page);
        request.setSize(size);

        Page<CollegeStudentProfile> studentsPage = userService.searchCollegeStudentsPaginated(request);

        List<CollegeStudentResponse> response = studentsPage.getContent().stream()
                .map(CollegeStudentResponse::fromProfile)
                .collect(Collectors.toList());

        // Return page metadata along with data
        return ResponseEntity.ok(Map.of(
                "content", response,
                "page", page,
                "size", size,
                "totalElements", studentsPage.getTotalElements(),
                "totalPages", studentsPage.getTotalPages(),
                "hasNext", studentsPage.hasNext()
        ));
    }

    // PUT /profile (Update your own profile)
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestBody ProfileUpdateRequest updateDetails,  // ✅ Changed DTO
            @AuthenticationPrincipal UserDetails currentUser) {

        String email = currentUser.getUsername();
        userService.updateUserProfile(email, updateDetails);  // ✅ Pass new DTO
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/profile/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        User user = userService.getUserByEmail(email);
        String result = userService.uploadProfileImage(user.getId(), image);
        return ResponseEntity.ok(result);
    }

    // ✅ NEW: Get profile image
    @GetMapping("/profile/image")
    public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        User user = userService.getUserByEmail(email);
        String image = userService.getProfileImage(user.getId());
        return ResponseEntity.ok(Map.of("profileImage", image));
    }
}