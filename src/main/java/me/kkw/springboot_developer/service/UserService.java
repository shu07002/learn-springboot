package me.kkw.springboot_developer.service;

import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.User;
import me.kkw.springboot_developer.dto.AddUserRequest;
import me.kkw.springboot_developer.respository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
}
