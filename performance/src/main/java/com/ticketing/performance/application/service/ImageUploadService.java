package com.ticketing.performance.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploadService {

    String upload(MultipartFile image) throws IOException;
}
