package com.javaweb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class FileStorageService {

    // Đường dẫn uploads dùng chung trên VPS
    private final String uploadDir = "/app/uploads";

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        // Nén ảnh nếu là JPEG hoặc PNG
        String contentType = file.getContentType();
        if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"))) {
            compressImage(file, filePath.toFile(), 0.6f); // nén nhẹ, giữ nét
        } else {
            // File không phải ảnh thì lưu bình thường
            file.transferTo(filePath.toFile());
        }
        // Trả về URL tương đối cho FE load
        return "/uploads/" + filename;
    }

    private void compressImage(MultipartFile file, File outputFile, float quality) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            file.transferTo(outputFile);
            return;
        }

        // Resize nhẹ nếu ảnh quá lớn (> 2000px)
        int maxWidth = 2000;
        if (image.getWidth() > maxWidth) {
            int newHeight = (image.getHeight() * maxWidth) / image.getWidth();
            BufferedImage resized = new BufferedImage(maxWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(image, 0, 0, maxWidth, newHeight, null);
            g.dispose();
            image = resized;
        }

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    public Long getFileSize(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) return 0L;

        String absolutePath;
        if (filePath.startsWith("/uploads/")) {
            String filename = filePath.substring("/uploads/".length());
            absolutePath = uploadDir + File.separator + filename;
        } else {
            absolutePath = filePath;
        }

        Path path = Paths.get(absolutePath);
        if (Files.exists(path)) {
            return Files.size(path);
        } else {
            System.err.println("File không tồn tại: " + absolutePath);
            return 0L;
        }
    }

    public Map<String, Object> getFileInfo(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) return Map.of();

        String absolutePath;
        if (filePath.startsWith("/uploads/")) {
            String filename = filePath.substring("/uploads/".length());
            absolutePath = uploadDir + File.separator + filename;
        } else {
            absolutePath = filePath;
        }

        Path path = Paths.get(absolutePath);
        if (!Files.exists(path)) {
            return Map.of("exists", false, "error", "File không tồn tại");
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
    }
}
