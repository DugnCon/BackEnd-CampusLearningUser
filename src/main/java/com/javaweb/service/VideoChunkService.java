package com.javaweb.service;

import com.javaweb.entity.Story.StoryEntity;
import com.javaweb.repository.IStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoChunkService {

    @Autowired
    private ICourseService courseService;
    @Autowired
    private IStoryRepository storyRepository;

    private final String uploadDir = "/app/uploads";

    public Map<String, Object> processVideoChunk(Long courseId, MultipartFile chunk,
                                                 Integer chunkIndex, Integer totalChunks,
                                                 String fileName) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (chunk.isEmpty()) {
                result.put("success", false);
                result.put("message", "Chunk file is empty");
                return result;
            }

            // Tạo thư mục upload nếu chưa tồn tại
            File outputDir = new File(uploadDir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String safeName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String tempFileName = "temp_" + courseId + "_" + safeName;
            File tempFile = new File(outputDir, tempFileName);

            // Append chunk vào file temp
            try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
                fos.write(chunk.getBytes());
            }


            // Nếu là chunk cuối cùng → hoàn thành upload
            if (chunkIndex.equals(totalChunks - 1)) {
                String finalName = System.currentTimeMillis() + "-" + fileName;
                File finalFile = new File(outputDir, finalName);

                if (tempFile.renameTo(finalFile)) {
                    String videoUrl = "/uploads/" + finalName;

                    // Update database
                    StoryEntity story = new StoryEntity();
                    story.setMediaUrl(videoUrl);
                    story.setMediaType("video");
                    storyRepository.save(story);

                    result.put("success", true);
                    result.put("videoUrl", videoUrl);
                    result.put("message", "Video uploaded successfully");
                } else {
                    try (FileOutputStream fos = new FileOutputStream(finalFile, true)) {
                        fos.write(chunk.getBytes()); // Write last chunk
                    }

                    // Xóa file temp sau khi copy thành công
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    String videoUrl = "/uploads/" + finalName;
                    StoryEntity story = new StoryEntity();
                    story.setMediaUrl(videoUrl);
                    story.setMediaType("video");
                    storyRepository.save(story);

                    result.put("success", true);
                    result.put("videoUrl", videoUrl);
                    result.put("message", "Video uploaded successfully (fallback)");
                }
            } else {
                // Chưa phải chunk cuối
                result.put("success", true);
                result.put("chunkIndex", chunkIndex);
                result.put("totalChunks", totalChunks);
                result.put("message", "Chunk uploaded successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Failed to upload chunk: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> cleanupOldTempFiles() {
        Map<String, Object> result = new HashMap<>();

        try {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                result.put("success", true);
                result.put("message", "Upload directory does not exist");
                result.put("deletedFiles", 0);
                return result;
            }

            long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
            int deletedCount = 0;

            // Chỉ xóa các file temp cũ (bắt đầu bằng "temp_")
            File[] files = uploadDirFile.listFiles((dir, name) -> name.startsWith("temp_"));
            if (files != null) {
                for (File file : files) {
                    if (file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                            System.out.println("🗑Deleted old temp file: " + file.getName());
                        } else {
                            System.err.println("Failed to delete: " + file.getName());
                        }
                    }
                }
            }

            // Dọn dẹp thư mục temp rỗng (nếu có)
            File tempDir = new File(uploadDir, "temp");
            if (tempDir.exists() && tempDir.isDirectory()) {
                File[] courseDirs = tempDir.listFiles();
                if (courseDirs != null && courseDirs.length == 0) {
                    if (tempDir.delete()) {
                        System.out.println("🗑️ Deleted empty temp directory");
                    }
                }
            }

            result.put("success", true);
            result.put("message", "Temp files cleanup completed");
            result.put("deletedFiles", deletedCount);

        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Cleanup failed: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> finalizeVideoUpload(Long courseId, String fileName, Integer totalChunks) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "This API is deprecated. Use chunk upload directly.");
        return result;
    }
}