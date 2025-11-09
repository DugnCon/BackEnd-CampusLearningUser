package com.javaweb.repository.impl.UserRepositoryCustom;

<<<<<<< HEAD
public interface UserRepositoryCustom {

=======
import com.javaweb.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {
    UserEntity getUserByUserID(Long userId);
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
}
