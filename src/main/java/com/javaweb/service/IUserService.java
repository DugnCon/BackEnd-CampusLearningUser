package com.javaweb.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.UserDTO;

@Service
public interface IUserService{
	ResponseEntity<Object> userRegister(UserDTO userDTO);
	ResponseEntity<Object> userLogin(UserDTO userDTO);
	ResponseEntity<Object> userLogout(Long userId);
	ResponseEntity<Object> userConnectGoogle(Long userId);
<<<<<<< HEAD
	UserDTO findOneByUserName(UserDTO userDTO);
=======
	ResponseEntity<Object> findAllUser(Long userId);
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
	UserEntity userVerifyByGoogle(String id,String email, String name, String picture, String username);
	
}
