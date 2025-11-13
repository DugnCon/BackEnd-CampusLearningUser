package com.javaweb.service.impl;
//import com.javaweb.model.dto.RoleDTO;


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
