package com.javaweb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.repository.IUserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
	@Autowired
	private IUserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.userLogin(username);
		if(user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		GrantedAuthority authorities = new SimpleGrantedAuthority(user.getRole());
		MyUserDetail myUserDetail = new MyUserDetail(username, user.getPassword(), true, true, true, true,  List.of(authorities));
		myUserDetail.setId(user.getUserId());
		myUserDetail.setProvider(user.getProviderID());
		myUserDetail.setProvider(user.getProvider());
		return myUserDetail;
	}

}
