package me.kkw.springboot_developer.service;


import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.RefreshToken;
import me.kkw.springboot_developer.respository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected token"));
    }
}
