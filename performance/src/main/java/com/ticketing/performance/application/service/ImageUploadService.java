package com.ticketing.performance.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    String upload(MultipartFile image);
}
