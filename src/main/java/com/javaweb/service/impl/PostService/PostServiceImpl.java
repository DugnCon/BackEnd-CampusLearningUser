package com.javaweb.service.impl.PostService;

import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.PostDTO;
import com.javaweb.repository.IPostLikeRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.IPostService;
import com.javaweb.service.SolvingByRabbitMQ.Media.MediaConsumer;
import com.javaweb.service.SolvingByRabbitMQ.Media.MediaProducer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class PostServiceImpl implements IPostService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private MediaProducer mediaProducer;
    @Autowired
    private MediaConsumer mediaConsumer;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private IPostLikeRepository postLikeRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public ResponseEntity<Object> createPost(PostDTO postDTO, Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found user in creating post"));
        try {
            PostEntity postEntity = modelMapper.map(postDTO, PostEntity.class);
            postEntity.setUser(userEntity);
            postRepository.save(postEntity);

            Long postId = postEntity.getPostID();

            List<String> fileNames = new ArrayList<>();

            for(MultipartFile file : postDTO.getMedia()) {
                String path = fileStorageService.saveFile(file);
                fileNames.add(path);
            }

            createMediaFromPost(postId, fileNames);

            return ResponseEntity.ok(Map.of("success", true, "message", "Created post successfully!"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void createMediaFromPost(Long postId, List<String> media) {
        mediaProducer.sendUploadTask(postId, media);
    }

    @Override
    public ResponseEntity<Object> getPostLimit(int limit, Long userId) {
        try {
            Pageable pageable = PageRequest.of(0, limit); // ví dụ limit = 20
            List<PostEntity> posts = postRepository.getPostLimit(pageable);
            List<PostEntity> postEntityList = new ArrayList<>();
            for(PostEntity data : posts) {
                UserEntity userEntity = data.getUser();
                String fullName = userEntity.getFullName();

                PostLikeEntity postLikeEntity = postLikeRepository.getPostLike(data.getPostID(), userId);

                data.setLiked(postLikeEntity != null);

                data.setFullName(fullName);
                postEntityList.add(data);
            }
            return ResponseEntity.ok(Map.of("posts" , postEntityList, "success", true, "message", "Thành công"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Object> getSinglePost(Long userId, Long postId) {
        try {
            PostEntity postEntity = postRepository.getSinglePost(userId, postId);
            return ResponseEntity.ok(Map.of("posts", postEntity, "success", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
