package com.edubridge.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileImageRequest {

    private MultipartFile image;  // File upload field
}