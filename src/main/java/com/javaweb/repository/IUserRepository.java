package com.javaweb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.UserEntity;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

	@Procedure(procedureName="user_register")
	String userRegister(String username, String email, String password, String fullname, String dateOfBirth, String school);

	@Query("select u from UserEntity u where Email = :email")
	UserEntity userLogin(@Param("email") String email);

	@Query("select u from UserEntity u join fetch u.courseEnrollment where u.UserID = :userId")
	UserEntity getCourseEnrollment(@Param("userId") Long userId);

	@Query("select u from UserEntity u where u.UserID = :userId")
	UserEntity getUserByUserID(@Param("userId") Long userId);

	@Query("""
    select u from UserEntity u
    left join FriendshipEntity f
      on (f.user.UserID = :currentUserId and f.friend.UserID = u.UserID)
      or (f.friend.UserID = :currentUserId and f.user.UserID = u.UserID)
    where u.UserID != :currentUserId
      and f.friendshipID is null
    """)
	List<UserEntity> findUsersNotConnected(@Param("currentUserId") Long userId);

	List<UserEntity> findByUsername(String username);
	List<UserEntity> findByFullName(String query);
	List<UserEntity> findBySchool(String query);

	@Query("SELECT u FROM UserEntity u WHERE u.UserID IN :userIds AND u.accountStatus = 'ACTIVE'")
	List<UserEntity> findByIdIn(@Param("userIds") List<Long> userIds);

	@Query(value = "SELECT u FROM UserEntity u WHERE u.accountStatus = 'ACTIVE' ORDER BY u.UserID DESC")
	List<UserEntity> findRecentActiveUsers(@Param("limit") int limit);

	@Query("SELECT u FROM UserEntity u WHERE " +
			"(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.school) LIKE LOWER(CONCAT('%', :query, '%'))) " +
			"AND u.accountStatus = 'ACTIVE'")
	List<UserEntity> searchUsers(@Param("query") String query);

	@Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.accountStatus = 'ACTIVE'")
	Optional<UserEntity> findByEmail(@Param("email") String email);

	@Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.username = :username")
	boolean existsByUsername(@Param("username") String username);

	@Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.email = :email")
	boolean existsByEmail(@Param("email") String email);

	@Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.accountStatus = 'ACTIVE'")
	Optional<UserEntity> findByUsernameExact(@Param("username") String username);

	@Query("SELECT u FROM UserEntity u WHERE u.UserID = :userId AND u.accountStatus = 'ACTIVE'")
	Optional<UserEntity> findActiveUserById(@Param("userId") Long userId);

	@Query("SELECT COUNT(u) FROM UserEntity u WHERE u.accountStatus = 'ACTIVE'")
	Long countActiveUsers();
}