package com.javaweb.model.dto.Friend;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SocketResponseDTO<T> {
    private String status; // "success", "error", "info"
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public SocketResponseDTO(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> SocketResponseDTO<T> success(String message, T data) {
        return new SocketResponseDTO<>("success", message, data);
    }

    public static <T> SocketResponseDTO<T> success(String message) {
        return new SocketResponseDTO<>("success", message, null);
    }

    public static <T> SocketResponseDTO<T> error(String message) {
        return new SocketResponseDTO<>("error", message, null);
    }

    public static <T> SocketResponseDTO<T> info(String message, T data) {
        return new SocketResponseDTO<>("info", message, data);
    }
}