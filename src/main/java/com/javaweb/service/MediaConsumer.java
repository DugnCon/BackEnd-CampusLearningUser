package com.javaweb.service;

import com.javaweb.config.RabbitMQConfig;
import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostMediaEntity;
import com.javaweb.repository.IPostMediaRepository;
import com.javaweb.repository.IPostRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MediaConsumer {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IPostMediaRepository postMediaRepository;

    //nơi đây sẽ tiếp nhận message để thực hiện tiến trình lưu riêng
    @RabbitListener(queues = RabbitMQConfig.MEDIA_UPLOAD_QUEUE)
    //@Transactional(rollbackFor = Exception.class)
    public void receiveUploadTask(Map<String, Object> message) throws IOException {
        Long postId = ((Number) message.get("postId")).longValue();
        List<String> files = (List<String>) message.get("fileNames");

        System.out.println("Processing upload for post " + postId);

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("not found user's post"));

        List<PostMediaEntity> postMediaEntityList = new ArrayList<>();

        //Sau khi tiếp nhận message thành công thì ta bắt đầu lưu lên cloud và sql
        for (String path : files) {
            System.out.println("Uploading file: " + path);

            PostMediaEntity postMediaEntity = new PostMediaEntity();
            postMediaEntity.setPosts(postEntity);
            postMediaEntity.setMediaUrl(path);

            if(path.endsWith(".mp4") || path.endsWith(".mp3") || path.endsWith("avi")) {
                postMediaEntity.setMediaType("video");
            } else {
                postMediaEntity.setMediaType("image");
            }

            postMediaEntityList.add(postMediaEntity);
        }

        postMediaRepository.saveAll(postMediaEntityList);

        System.out.println("Upload complete for post " + postId);
    }
}

