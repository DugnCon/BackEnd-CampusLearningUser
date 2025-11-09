package com.javaweb.repository;

import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.UserDTO;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
	@Procedure(procedureName="user_register")
	String userRegister(String username, String email, String password, String fullname, String dateOfBirth, String school);
	
	/*@Procedure(name="UserEntity.userLogin")
	Map<String,Object> userLogin(@Param("u_email") String email);*/

	UserDTO findByUsername(String username);
	Optional<UserEntity> findUserEntityByUsername(String username);
	Optional<UserEntity> findUserEntityByEmail(String email);

	@Query("select u from UserEntity u where Email = :email")
	UserEntity userLogin(@Param("email") String email);
	@Query("select u from UserEntity u join fetch u.courseEnrollment where u.UserID = :userId")
	UserEntity getCourseEnrollment(@Param("userId") Long userId);
	@Query("select u from UserEntity u where u.UserID = :userId")
	UserEntity getUserByUserID(@Param("userId") Long userId);
}