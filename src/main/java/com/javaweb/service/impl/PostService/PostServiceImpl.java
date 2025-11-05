package com.javaweb.service.impl.PostService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.Post.PostCommentDTO;
import com.javaweb.model.dto.Post.PostDTO;
import com.javaweb.model.dto.Post.PostDetailDTO;
import com.javaweb.model.dto.Post.PostMediaDTO;
import com.javaweb.repository.ICommentRepository;
import com.javaweb.repository.IPostLikeRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.IPostService;
import com.javaweb.service.SolvingByRabbitMQ.Media.MediaConsumer;
import com.javaweb.service.SolvingByRabbitMQ.Media.MediaProducer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;


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
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("commentRedisTemplate")
    private RedisTemplate<String, String> commentRedisTemplate;
    @Autowired
    private ICommentRepository commentRepository;

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
            Pageable pageable = PageRequest.of(0, limit);
            List<PostEntity> posts = postRepository.getPostLimit(pageable);
            List<PostDetailDTO> postEntityList = new ArrayList<>();

            for(PostEntity data : posts) {
                UserEntity userEntity = data.getUser();
                String fullName = userEntity.getUsername();
                String userImage = userEntity.getAvatar();

                // Tạo DTO và set thủ công tất cả
                PostDetailDTO postDetailDTO = new PostDetailDTO();

                // Set basic post info
                postDetailDTO.setPostID(data.getPostID());
                postDetailDTO.setContent(data.getContent());
                postDetailDTO.setType(data.getType());
                postDetailDTO.setVisibility(data.getVisibility());
                postDetailDTO.setLocation(data.getLocation());
                postDetailDTO.setCreatedAt(data.getCreatedAt());
                postDetailDTO.setUpdatedAt(data.getUpdatedAt());
                postDetailDTO.setLikesCount(data.getLikesCount());
                postDetailDTO.setCommentsCount(data.getCommentsCount());
                postDetailDTO.setSharesCount(data.getSharesCount());
                postDetailDTO.setReportsCount(data.getReportsCount());

                // Set user info
                postDetailDTO.setFullName(fullName);
                postDetailDTO.setUserName(userEntity.getUsername());
                postDetailDTO.setUserImage(userImage);

                //Set media cho post
                Set<PostMediaDTO> postMediaDTO = data.getMedia().stream().map(media -> modelMapper.map(media, PostMediaDTO.class)).collect(Collectors.toSet());
                postDetailDTO.setMedia(postMediaDTO);

                PostLikeEntity postLikeEntity = postLikeRepository.getPostLike(data.getPostID(), userId);
                postDetailDTO.setLiked(postLikeEntity != null);

                // Lấy comments từ Redis
                List<PostCommentDTO> comments = getCommentsFromRedis(data.getPostID());
                postDetailDTO.setComment(comments);

                postEntityList.add(postDetailDTO);
            }
            return ResponseEntity.ok(Map.of("posts", postEntityList, "success", true, "message", "Thành công"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<PostCommentDTO> getCommentsFromRedis(Long postId) throws JsonProcessingException {
        List<PostCommentDTO> comments = new ArrayList<>();
        String postCommentsKey = "post:" + postId + ":comments";

        // Lấy danh sách commentId từ Redis
        List<String> commentIds = commentRedisTemplate.opsForList().range(postCommentsKey, 0, 99);

        if (commentIds != null && !commentIds.isEmpty()) {
            for (String commentId : commentIds) {
                String commentDetailKey = "comment:detail:" + commentId;
                String jsonComment = commentRedisTemplate.opsForValue().get(commentDetailKey);

                if (jsonComment != null) {
                    // Có comment trong Redis
                    PostCommentDTO commentDTO = objectMapper.readValue(jsonComment, PostCommentDTO.class);
                    comments.add(commentDTO);
                } else {
                    // Fallback: lấy từ database nếu không có trong Redis
                    CommentEntity commentEntity = commentRepository.findById(Long.parseLong(commentId))
                            .orElse(null);
                    if (commentEntity != null && !commentEntity.getIsDeleted()) {
                        PostCommentDTO commentDTO = convertCommentEntityToDTO(commentEntity);
                        comments.add(commentDTO);

                        // Optional: Có thể update lại Redis để lần sau có data
                        try {
                            String updatedJsonComment = objectMapper.writeValueAsString(commentDTO);
                            commentRedisTemplate.opsForValue().set(commentDetailKey, updatedJsonComment);
                        } catch (Exception e) {
                            System.err.println("Failed to update Redis for comment " + commentId);
                        }
                    }
                }
            }
        } else {
            // Nếu Redis không có danh sách comment, lấy từ database
            comments = getCommentsFromDatabase(postId);

            // Optional: Có thể cache lại vào Redis
            cacheCommentsToRedis(postId, comments);
        }

        // Sắp xếp comments theo thứ tự mới nhất trước
        comments.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return comments;
    }

    private List<PostCommentDTO> getCommentsFromDatabase(Long postId) {
        // Lấy comments từ database (có thể thêm pagination nếu cần)
        List<CommentEntity> commentEntities = commentRepository.findCommentsByPostId(postId);

        return commentEntities.stream()
                .map(this::convertCommentEntityToDTO)
                .collect(Collectors.toList());
    }

    private PostCommentDTO convertCommentEntityToDTO(CommentEntity commentEntity) {
        PostCommentDTO commentDTO = new PostCommentDTO();
        commentDTO.setCommentID(commentEntity.getCommentID());
        commentDTO.setContent(commentEntity.getContent());
        commentDTO.setCreatedAt(commentEntity.getCreatedAt());
        commentDTO.setLikesCount(commentEntity.getLikesCount());

        // Set user info
        UserEntity user = commentEntity.getUser();
        if (user != null) {
            commentDTO.setUserID(user.getUserID());
            commentDTO.setFullName(user.getFullName());
            commentDTO.setUserImage(user.getAvatar()); // hoặc getImage() tùy entity
        }

        return commentDTO;
    }

    private void cacheCommentsToRedis(Long postId, List<PostCommentDTO> comments) {
        try {
            String postCommentsKey = "post:" + postId + ":comments";

            // Xóa cache cũ nếu có
            commentRedisTemplate.delete(postCommentsKey);

            // Cache từng comment detail
            for (PostCommentDTO comment : comments) {
                String commentDetailKey = "comment:detail:" + comment.getCommentID();
                String jsonComment = objectMapper.writeValueAsString(comment);
                commentRedisTemplate.opsForValue().set(commentDetailKey, jsonComment);

                // Thêm commentId vào danh sách post comments (chỉ 100 comment mới nhất)
                commentRedisTemplate.opsForList().leftPush(postCommentsKey, String.valueOf(comment.getCommentID()));
            }

            // Giữ chỉ 100 comment mới nhất
            commentRedisTemplate.opsForList().trim(postCommentsKey, 0, 99);

        } catch (Exception e) {
            System.err.println("Failed to cache comments to Redis for post " + postId);
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
