package com.javaweb.service.impl.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IUserService;
import com.javaweb.service.JwtService;

@Service
public class UserServiceImpl implements IUserService{
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

	@Override
	@Transactional
	public ResponseEntity<Object> userRegister(UserDTO userDTO) {
		if (userDTO == null ||
	        userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty() ||
	        userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty() ||
	        userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty() ||
	        userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty() ||
	        userDTO.getDateOfBirth() == null || userDTO.getDateOfBirth().trim().isEmpty() ||
	        userDTO.getSchool() == null || userDTO.getSchool().trim().isEmpty()
	    ) {
	        Map<String, Object> error = new HashMap<>();
	        error.put("status", HttpStatus.BAD_REQUEST.value());
	        error.put("message", "All fields are required and cannot be null or empty");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	    } else {
	    	String password = passwordEncoder.encode(userDTO.getPassword());
	    	String alert = userRepository.userRegister(userDTO.getUsername(), userDTO.getEmail(), 
					password, userDTO.getFullName(), 
					userDTO.getDateOfBirth(), userDTO.getSchool());
			if(alert.equals("success")) {
				return ResponseEntity.ok().body(Map.of("success","Signup Successfully!"));
			} else if(alert.equals("wrong")){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("massage",HttpStatus.BAD_REQUEST.value()));
			} else {
				return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message",HttpStatus.TOO_MANY_REQUESTS.value()));
			}
	    }
	}

	@Override
	@Transactional
	public UserDTO findOneByUserName(UserDTO userDTO) {
		return userRepository.findByUsername(userDTO.getUsername());
	}

	@Override
	@Transactional
	public ResponseEntity<Object> userLogin(UserDTO userDTO) {
		String email = userDTO.getEmail();
		String password = userDTO.getPassword();
		if(!email.contains("@")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST.value());
		}
		try {
			UserEntity user = userRepository.userLogin(email);
			if(passwordEncoder.matches(password, user.getPassword())) {
				
				user.setStatus("ONLINE");
				userRepository.save(user);
				
				String token = jwtService.generateTokenWithClaims(Map.of(
						"id", user.getUserId(),
						"email",user.getEmail(),
						"role", user.getRole()
				));
				return ResponseEntity.ok(Map.of(
					"user", user,
					"token", token
				));
				
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST.value());
			}
		} catch (Exception e) {
			throw new RuntimeException(e + " can not found user");
		}
	}

	@Override
	public UserEntity userVerifyByGoogle(String id,String email, String name, String picture, String username) {
		UserEntity user = userRepository.userLogin(email);
		if(user == null) {
			UUID uuid = UUID.randomUUID();
			String password = uuid.toString();
			
			UserEntity userEntity = new UserEntity();
			userEntity.setEmail(email);
			userEntity.setUsername(username);
			userEntity.setFullName(name);
			userEntity.setAvatar(picture);
			userEntity.setEmailVerified(true);
			userEntity.setPassword(password);
			userEntity.setSchool("None");
			userEntity.setProvider("google");
			userEntity.setProviderID(id);
			
			userRepository.save(userEntity);
			return userEntity;
		} else {
			user.setStatus("ONLINE");
			userRepository.save(user);
			return user;
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> userLogout(Long userId) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found in logout"));
		try {
			//user.setAccountStatus("OUT");
			user.setStatus("OFFLINE");
			userRepository.save(user);
			return ResponseEntity.ok(Map.of("success", true, "message", "logout"));
		} catch (Exception e) {
			throw new RuntimeException(e + " can not logout account from user");
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> userConnectGoogle(Long userId) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found in logout"));
		try {
			//user.setAccountStatus("OUT");
			user.setStatus("ONLINE");
			userRepository.save(user);
			return ResponseEntity.ok(Map.of("success", true, "message", "Login by google successfully!"));
		} catch (Exception e) {
			throw new RuntimeException(e + " can not logout account from user");
		}
	}

}
