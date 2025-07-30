package me.kkw.springboot_developer.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/*
* 목적
* TokenProvider의 목적은 JWT의 생성과 유효성 검증에 관련한
* 모든 로직을 처리하는 것이다.
* JWT token을 만들고, 받은 JWT가 찐인지 짭인지 구별한다.
*/

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();

        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    /*
    * `Jwts.builder()`
    * jjwt 라이브러리에서 제공하는 기능으로 "지금부터 JWT를 만들기 시작한다"는 뜻
    * Builder 패턴으로 시작함
    * 메소드들을 체인으로 연결해서 객체를 설정한다.
    *
    * `.setHeaderParam(Header.TYPE, Header.JWT_TYPE)`
    * JWT의 헤더를 설정한다.
    * Header.TYPE: 이 헤더의 typ 필드를 의미한다.
    * Header.JWT_TYPE: 그 값을 JWT로 설정한다
    * 이 토큰은 JWT 형식이라고 명시하는 것
    *
    * `.setIssuer(jwtProperties.getIssuer())`
    * 페이로드의 iss(issuer) 클레임을 설정한다
    * 이 토큰은 누가 발급했는가를 나타낸다.
    * `jwtProperties.getIssuer()`는 JwtProperties 클래스를 통해
    * application.yml에서 읽어온 issuer 정보를 가져와서 설정한다.
    *
    * `.setIssuedAt(now)`
    * 페이로드의 iat(Issued At) 클레임을 설정한다.
    * 이 토큰은 언제 발급되었는지를 나타낸다.
    * now는 메소드가 호출된 현재 시간을 Date 객체로 전달받아 설정힌디.
    *
    * `.setExpiration(expiry)`
    * 페이로드의 exp(Expiration Time) 클레임을 설정한다.
    * 이 토큰이 언제 만료되는지를 나타낸다.
    * expiry는 generateToken 메소드에서 "현재 기간 + 유효 기간" 으로
    * 계산된 만료 시간을 Date 객체로 전달받아 설정한다.
    * 이 시간이 지나면 토큰은 유효하지 않는 것으로 간주된다.
    *
    * `.setSubject(user.getEmail())`
    * 페이로드의 sub(Subject) 클레임을 설정한다
    * 이 토큰의 주인공이 누구인지를 의미한다.
    * `user.getEmail()`으로 토큰의 주체를 사용자의 이메일로 설정한다
    * 이메일처럼 고유한 값을 사용하면 나중에 토큰만으로도 누구인지 식별하기 쉽다.
    *
    * `.claim("id", user.getId())`
    * 페이로드에 비공개 클레임을 추가한다.
    * 표준 스펙에는 없지만, 우리가 직접 정의해서 추가하고 싶은 정보를 담을 때 사용한다.
    * "id", "user.getId()"는 id라는 이름으로 사용자의 소유 ID(user.getId())를 저장한다.
    * 나중에 토큰을 받았을 때 데이터베이스를 다시 조회하지 않고도 토큰에서 바로 사용자 ID를 꺼낼 수 있다.
    *
    * `.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())`
    * JWT의 서명 부분을 생성한다.
    * 이 토큰이 위변조되지 않았음을 보장하는 가장 중요한 부분이다
    * 헤더와 페이로드를 합친 후, 지정된 알고리즘과 Secret Key를 사용해서 암호화한다.
    * `SignatureAlgorithm.HS256`은 HS256이라는 대칭키 암호화 알고리즘을 사용하겠다고 지정한다.
    * `jwtProperties.getSecretKey()`는 application.yml에서 읽어온 비밀 키를 서명에 사용한다.
    * 이 키는 서명을 모르면 위조할 수 없다.
    *
    * `.compact()`
    * 지금까지 설정한 헤더, 페이로드, 서명을 합쳐서 실제 JWT 문자열로 만든다
    * 결과로는 xxxxx.yyyyy.zzzzz 처럼 .으로 구분된 Base64로 인코딩된 긴 문자열 형태로
    * 최종 JWT가 생성되고 반환된다.
     */
    private String makeToken (Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    /*
    * 주어진 토큰이 유효한지 검증하는 역할
    *
    * Jwts.parser()
    * 호출하면 JwtParser라는 객체가 반환된다
    * 이 파서 객체는 연속되는 설정들을 체인형태로 받아서 JWT를 검증하고
    * 그 내용(클레임)을 추출하는 역할을 한다.
    *
    * `.setSigningKey(jwtProperties.getSecretKey())
    * 토큰을 검증할 때 사용할 비밀 키를 파서(parser)에게 알려준다.
    * 파서는 전달받은 token의 헤더와 페이로드를 가져온다.
    * 그리고 setSigningkey로 설정된 서버의 비밀 키를 사용해서
    * 토큰을 만들 때와 동일한 알고리즘(HS256)으로 서명을 다시 계산한다.
    * 새로 계산한 서명과 원래 token에 붙어있던 서명을 비교한다.
    * 두 서명이 일치하면 토큰이 위변조 X
    * 두 서명이 다르면 토큰이 위변조 O -> SignatureException 발생
    *
    * `.parseClaimsJws(token)`
    * 실직적인 모든 검증 작업이 한 번에 일어난다
    * 서명 검증 -> 방금 위에서 설명한 서명 비교 작업
    * 만료 시간 검증 -> 만료 시간 유효한지 확인하고 만료면 ExpiredJwtException
    * 형식 검증 -> 주어진 token 문자열이 xxxxx.yyyyy.zzzzz 형식을 만족하는 지 확인
    * 틀리면 MalformedJwtException 발생
    */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
    * 유효성이 검증된 JWT 토큰을
    * 스프링 시큐리티가 이해할 수 있는 인증 객체로 변환하는 역할읋 한다
    *
    * Claims claims = getClaims(token);
    * 토큰에서 클레임(정보)들을 추출한다.
    * getClaims는 맨 밑에 있음
    *
    * `Set<SimpleGrantedAuthority> authorities = ...`
    * `SimpleGrantedAuthority`는 스프링 시쿠리티에서 권한을 나타내는 가장
    * 기본적인 클래스다.
    * 관례적으로 권한 이름 앞에 ROLE_을 붙인다. ROLE_USER, ROLE_ADMIN, ...
    * `Collections.singleton(...)`는 단 하나의 요소만 포함하는 Set을 
    * 간단하게 만드는 방법이다.
    * 여기서는 ROLE_USER라는 권한 하나만 만들어서 authorities라는 set에 담는다
    * 이 토큰을 가진 사용자는 USER 역학을 수행할 수 있는 권한을 가진다
    *
    * `return new UsernamePasswordAuthenticationToken(...)`
    * 최종 목표인 Authetication 객체를 생성해서 반환한다.
    * UsernamePasswordAuthenticationToken은 Authentication 인터페이스의
    * 가장 일반적인 구현체다.
    * 인자로는 3가지를 받는다.
    * 1. principal: 인증된 사용자가 누구인지에 대한 저옵
    * ...userDetail.User(...)은 스프링 시큐리티가 사용하는 표준 사용자 정보 객체
    *   - claims.getSubject(): 토큰의 sub 클레임을 가져와서 사용자의 이름(username)으로 설정
    *   - "": 비밀번호(password) 지리, 이미 JWT로 인증을 마쳤으니까 비밀번호 필요 없음
    *   - authorities: 위에서 만든 권한 목록을 전달
    * 2. credentials: 사용자가 무엇으로 자신을 증명했는지에 대한 정보
    *   - token: 여기서는 인증에 사용된 JWT 토큰 자체를 자격 증명으로 설정
    * 3. authorities: 이 사용자가 무엇을 할 수 있는지에 대한 정보
    *   - 위에서 만든 authorities Set을 다시 전달
    */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections
                .singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails
                        .User(claims.getSubject(),"", authorities), token, authorities);
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        Claims claim = Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();

        System.out.println(claim);
        return claim;
    }
}
