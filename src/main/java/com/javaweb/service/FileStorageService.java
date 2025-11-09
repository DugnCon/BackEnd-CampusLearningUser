package com.javaweb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
=======
import java.util.Map;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

@Service
public class FileStorageService {

    // Đường dẫn tuyệt đối
<<<<<<< HEAD
    private final String uploadDir = "C:/Esclipe_Web/campuslearning/uploads";
=======
    private final String uploadDir = "C:/Esclipe_Web/campuslearning-addmin/uploads";
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c

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
<<<<<<< HEAD
}



=======

    //Cái này để lấy file size
    public Long getFileSize(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            return 0L;
        }

        try {
            //convert relative path to absolute path
            String absolutePath;
            if (filePath.startsWith("/uploads/")) {
                // Nếu là relative path từ FE
                String filename = filePath.substring("/uploads/".length());
                absolutePath = uploadDir + File.separator + filename;
            } else {
                //nếu đã là absolute path
                absolutePath = filePath;
            }

            Path path = Paths.get(absolutePath);
            if (Files.exists(path)) {
                return Files.size(path);
            } else {
                System.err.println("File không tồn tại: " + absolutePath);
                return 0L;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy file size: " + e.getMessage());
            return 0L;
        }
    }

    //cái này lấy fileinfo cho nó chi tiết
    public Map<String, Object> getFileInfo(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            return Map.of();
        }

        try {
            // Convert relative path to absolute path
            String absolutePath;
            if (filePath.startsWith("/uploads/")) {
                String filename = filePath.substring("/uploads/".length());
                absolutePath = uploadDir + File.separator + filename;
            } else {
                absolutePath = filePath;
            }

            Path path = Paths.get(absolutePath);
            if (!Files.exists(path)) {
                return Map.of(
                        "exists", false,
                        "error", "File không tồn tại"
                );
            }

            String fileName = path.getFileName().toString();
            String fileExtension = "";
            if (fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }

            long fileSize = Files.size(path);
            String mimeType = Files.probeContentType(path);

            return Map.of(
                    "exists", true,
                    "originalName", fileName,
                    "size", fileSize,
                    "extension", fileExtension,
                    "mimetype", mimeType != null ? mimeType : "application/octet-stream",
                    "absolutePath", absolutePath,
                    "relativePath", filePath
            );

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy file info: " + e.getMessage());
            return Map.of(
                    "exists", false,
                    "error", e.getMessage()
            );
        }
    }
}
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
