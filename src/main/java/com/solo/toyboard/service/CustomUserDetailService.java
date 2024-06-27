package com.solo.toyboard.service;

import com.solo.toyboard.dto.CustomUserDetails;
import com.solo.toyboard.entity.UserEntity;
import com.solo.toyboard.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//LoginFilter을 사용하기 위해 CustomUserDetails를 불러오는 기능
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userData = userRepository.findByUsername(username);

        if(userData == null) return null;

        return new CustomUserDetails(userData);
    }
}
