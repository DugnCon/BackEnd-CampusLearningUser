package com.javaweb.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Arrays;

@Service
public class CodeServerService {

    private final String dockerImage = "codercom/code-server:latest";
    private int nextPort = 8100;   // port host bắt đầu
    private final Map<String, CodeServerInstance> instances = new ConcurrentHashMap<>();
    private final String defaultPassword = "changeme";

    /**
     * Khởi tạo code-server cho học viên
     */
    public synchronized String initialize(String courseId, String lessonId, String userId) throws IOException {
        String key = courseId + ":" + lessonId + ":" + userId;

        // Nếu container đã chạy, trả URL
        if (instances.containsKey(key)) {
            CodeServerInstance instance = instances.get(key);
            instance.lastUsed = Instant.now();
            return instance.getUrl();
        }

        int port = nextPort++;

        // Docker run command với PASSWORD
        List<String> cmd = Arrays.asList(
                "docker", "run", "-d",
                "-p", port + ":8080",
                "--name", "cs-" + courseId + "-" + lessonId + "-" + userId,
                "-e", "PASSWORD=" + defaultPassword,
                dockerImage
        );

        new ProcessBuilder(cmd).start();

        CodeServerInstance instance = new CodeServerInstance(port, defaultPassword);
        instances.put(key, instance);

        return instance.getUrl();
    }

    /**
     * Cleanup container idle > maxIdleMinutes
     */
    @Scheduled(fixedDelay = 60_000) // mỗi 1 phút
    public void cleanupScheduled() {
        cleanup(10); // default 10 phút
    }

    public synchronized void cleanup(int maxIdleMinutes) {
        Instant now = Instant.now();
        for (Map.Entry<String, CodeServerInstance> entry : instances.entrySet()) {
            CodeServerInstance instance = entry.getValue();
            if (now.minusSeconds(maxIdleMinutes * 60L).isAfter(instance.lastUsed)) {
                try {
                    new ProcessBuilder("docker", "rm", "-f",
                            "cs-" + entry.getKey().replace(":", "-")).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                instances.remove(entry.getKey());
            }
        }
    }

    private static class CodeServerInstance {
        int port;
        Instant lastUsed;
        String password;

        CodeServerInstance(int port, String password) {
            this.port = port;
            this.lastUsed = Instant.now();
            this.password = password;
        }

        String getUrl() {
            return "http://localhost:" + port + "/";
        }
    }
}

