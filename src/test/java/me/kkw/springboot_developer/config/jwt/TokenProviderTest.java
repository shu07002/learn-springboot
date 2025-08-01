package me.kkw.springboot_developer.config.jwt;

import io.jsonwebtoken.Jwts;
import me.kkw.springboot_developer.domain.User;
import me.kkw.springboot_developer.respository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 벙보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        User testUser = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        Assertions.assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_inValidToken() {
        String token = JwtFactory.builder()
                .expiration(new Date(new Date()
                        .getTime() - Duration.ofDays(7).toMillis()))
                .build().createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token);

        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰일 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        String token = JwtFactory.withDefaultValue().createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token);

        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        Authentication authentication = tokenProvider.getAuthentication(token);

        Assertions.assertThat(((UserDetails) authentication.getPrincipal()).getUsername())
                .isEqualTo(userEmail);
    }


    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        Long userIdByToken = tokenProvider.getUserId(token);

        Assertions.assertThat(userIdByToken).isEqualTo(userId);
    }
}
