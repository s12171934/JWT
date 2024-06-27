package com.solo.toyboard.service;

import com.solo.toyboard.dto.JoinDTO;
import com.solo.toyboard.entity.UserEntity;
import com.solo.toyboard.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //회원가입
    public boolean joinProcess(JoinDTO joinDTO) {
        UserEntity userData = new UserEntity();

        boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());
        if(isUser) return false;

        userData.setUsername(joinDTO.getUsername());
        String encodingPassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());
        userData.setPassword(encodingPassword);
        userData.setRole("ROLE_USER");

        userRepository.save(userData);
        return true;
    }
}
