package me.kkw.springboot_developer.config.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/*
* 목적
* JwtProperties의 목적은 JWT 관련 설정 값들을 코드에서 분리하고
* application.yml에서 관리하는 것
* JWT를 사용하려면 issuer와 secret_key가 필요함.
* - issuer: 이토큰을 누가 발급했는지 나타내는 문자열
* - secret_key: 토큰의 서명을 생성하고 검증하는 데 사용되는 매우 중요한 키
* JwtProperties는 설정 정보 담는 그릇
* 스프링 부트가 이 객체 만들고 application.yml에서 해당 값들을 자동으로 읽고 채워줌
*/

@Setter
@Getter
/*
* @ConfigurationProperties("jwt")
* 이 클래스는 외부 설정 파일의 속성들을 담는 용도로 쓰인다는걸 스프링 부트에 알려준다
* application.yml 파일에서 jwt라는 이름으로 시작하는 속성들을 찾아서
* 이 클래스의 필드에 자동으로 매핑하라고 지시한다.
* jwt.issuer는 JwtProperties의 issuer 필드에 주입된다.
* jwt.secret_key는 JwtProperties의 secretKey 필드에 주입된다.
*/
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
