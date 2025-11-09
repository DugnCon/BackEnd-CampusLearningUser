package com.javaweb.repository.impl.UserRepositoryCustom;

import com.javaweb.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {
    UserEntity getUserByUserID(Long userId);
}