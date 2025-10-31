package com.javaweb.service.impl;
import com.javaweb.model.dto.MyUserDetail;
//import com.javaweb.model.dto.RoleDTO;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    //@Override
    //Spring security tự động gọi method này 
    //public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    	// Lấy dữ liệu của user
    	//UserDTO userDTO2 = userService.findOneByUserName(name);
    	//Integer status = userDTO2.getStatus();
        //UserDTO userDTO = userService.findOneByUserNameAndStatus(name, status);
        /*if(userDTO == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(RoleDTO role: userDTO.getRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+ role.getCode()));
        }
        MyUserDetail myUserDetail = new MyUserDetail(name,userDTO.getPassword(),true,true,true,true,authorities);
        BeanUtils.copyProperties(userDTO, myUserDetail); // Dùng để coppy getter/setter giữa 2 class khá giống ModelMapper
        return myUserDetail
    }
}*/
