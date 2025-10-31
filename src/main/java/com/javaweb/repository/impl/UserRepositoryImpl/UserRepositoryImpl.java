package com.javaweb.repository.impl.UserRepositoryImpl;

import com.javaweb.entity.UserEntity;
import com.javaweb.repository.impl.UserRepositoryCustom.UserRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public UserEntity getUserByUserID(Long userId) {
        String sql = "select UserID, Username, Email  from users";
        return null;
    }
}
