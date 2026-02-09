package com.edubridge.backend.controller;

import com.edubridge.backend.model.College;
import com.edubridge.backend.service.CollegeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/college")  // Base path for all endpoints
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @Data
    public static class CreateCollegeRequest {
        @NotBlank(message = "College name is required")
        private String name;
        private String location;
        private String description;
    }

    // Get all colleges - NO AUTH REQUIRED
    @GetMapping("/all")
    public ResponseEntity<?> getAllColleges() {
        List<College> colleges = collegeService.getAllColleges();
        return ResponseEntity.ok(colleges);
    }

    // Create a college - NO AUTH REQUIRED (for now)
    @PostMapping("/create")
    public ResponseEntity<?> createCollege(@Valid @RequestBody CreateCollegeRequest request) {
        College college = collegeService.createCollege(
                request.getName(),
                request.getLocation(),
                request.getDescription()
        );
        return ResponseEntity.ok(college);
    }

    // Get college by ID - NO AUTH REQUIRED
    @GetMapping("/{id}")
    public ResponseEntity<?> getCollegeById(@PathVariable Long id) {
        College college = collegeService.getCollegeById(id);
        return ResponseEntity.ok(college);
    }
}