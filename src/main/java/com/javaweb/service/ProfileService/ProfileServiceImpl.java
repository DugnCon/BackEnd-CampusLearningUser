package com.javaweb.service.ProfileService;

import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.Post.PostProfileDTO;
import com.javaweb.model.dto.Post.PostMediaDTO;
import com.javaweb.repository.IPostLikeRepository;
import com.javaweb.repository.IPostRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.FileStorageService;
import com.javaweb.service.IProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class ProfileServiceImpl implements IProfileService {
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IPostLikeRepository postLikeRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public ResponseEntity<?> getMediaUser(Long userId, Long targetUserId, int limit) {
        Pageable pageable = (Pageable) PageRequest.of(0, limit);
        List<PostProfileDTO> posts = new ArrayList<>();
        if(Objects.equals(userId,targetUserId)) {
            UserEntity user = userRepository.findById(userId).orElseThrow();
            List<PostEntity> postEntityList = postRepository.getProfilePostLimit(userId, pageable);
            for(PostEntity data : postEntityList) {

                PostLikeEntity postLikeEntity = postLikeRepository.getPostLike(data.getPostID(), userId);
                data.setLiked(postLikeEntity != null);

                PostProfileDTO postProfileDTO = modelMapper.map(data, PostProfileDTO.class);
                postProfileDTO.setPrivacy(data.getVisibility());
                postProfileDTO.setUserID(user.getUserID());
                postProfileDTO.setFullName(user.getFullName());
                postProfileDTO.setAvatar(user.getAvatar());
                postProfileDTO.setImage(user.getImage());

                Set<PostMediaDTO> postMediaDTOS = data.getMedia().stream().map(media -> modelMapper.map(media, PostMediaDTO.class)).collect(Collectors.toSet());
                postProfileDTO.setMedia(postMediaDTOS);

                posts.add(postProfileDTO);
            }
        } else {
            UserEntity user = userRepository.findById(targetUserId).orElseThrow();
            List<PostEntity> postEntityList = postRepository.getProfilePostLimit(targetUserId, pageable);
            for(PostEntity data : postEntityList) {
                if("public".equals(data.getVisibility())) {
                    PostLikeEntity postLikeEntity = postLikeRepository.getPostLike(data.getPostID(), targetUserId);
                    data.setLiked(postLikeEntity != null);

                    PostProfileDTO postProfileDTO = modelMapper.map(data, PostProfileDTO.class);
                    postProfileDTO.setPrivacy(data.getVisibility());
                    postProfileDTO.setUserID(user.getUserID());
                    postProfileDTO.setFullName(user.getFullName());
                    postProfileDTO.setAvatar(user.getAvatar());
                    postProfileDTO.setImage(user.getImage());

                    Set<PostMediaDTO> postMediaDTOS = data.getMedia().stream().map(media -> modelMapper.map(media, PostMediaDTO.class)).collect(Collectors.toSet());
                    postProfileDTO.setMedia(postMediaDTOS);

                    posts.add(postProfileDTO);
                }
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "posts", posts));
    }
    @Override
    public ResponseEntity<?> updateAvatar(MultipartFile file, Long userId) throws IOException {
        try {
            String path = fileStorageService.saveFile(file);
            UserEntity user = userRepository.findById(userId).orElseThrow();

            user.setAvatar(path);
            user.setImage(path);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("profileImage", path, "success", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
