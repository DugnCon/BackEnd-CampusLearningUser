package com.javaweb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    // Đường dẫn tuyệt đối
    private final String uploadDir = "C:/Esclipe_Web/campuslearning/uploads";

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // tạo folder nếu chưa có
        }

        String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        file.transferTo(filePath.toFile());

        // Trả về URL tương đối để FE load
        return "/uploads/" + filename;
    }
}



