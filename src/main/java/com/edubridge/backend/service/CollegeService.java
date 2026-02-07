package com.edubridge.backend.service;

import com.edubridge.backend.exception.BadRequestException;
import com.edubridge.backend.exception.ResourceNotFoundException;
import com.edubridge.backend.model.College;
import com.edubridge.backend.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // ✅ This is critical - makes it a Spring service
public class CollegeService {

    @Autowired
    private CollegeRepository collegeRepository;

    public List<College> getAllColleges() {
        return collegeRepository.findAll();
    }

    public College getCollegeById(Long id) {
        return collegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + id));
    }

    public College createCollege(String name, String location, String description) {
        if (collegeRepository.existsByName(name)) {
            throw new BadRequestException("College with name '" + name + "' already exists");
        }

        College college = new College();
        college.setName(name);
        college.setLocation(location);
        college.setDescription(description);

        return collegeRepository.save(college);
    }
}