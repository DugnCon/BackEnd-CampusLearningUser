package com.javaweb.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.javaweb.model.dto.MyUserDetail;


@Configuration
public class SecurityUtils { //Config security
	public static MyUserDetail getPrincipal() {
		/**
		 * SecurityContextHolder là nơi Spring Security lưu trữ thông 
		 * tin authentication 
		 * hiện tại (ai đang login).**/
	        return (MyUserDetail) (SecurityContextHolder
	                .getContext()).getAuthentication().getPrincipal();
	}
	//Lấy danh sách Role của user hiện tại 
	public static List<String> getAuthorities() {
	        List<String> results = new ArrayList<>();
	        List<GrantedAuthority> authorities = (List<GrantedAuthority>)(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
	        for (GrantedAuthority authority : authorities) {
	            results.add(authority.getAuthority());
	        }
	        return results;
	 }
}
