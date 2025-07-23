package me.kkw.springboot_developer.config;

import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.service.UserDetailService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 웹 보안 기능 활성화 의미, 이걸 켜야 스프링 시큐리티가 동작
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 스프링 시큐리티의 기능 비활성화 영역
    /*
    * 특정 경로 요청에 대해 스프링 시큐리티의 모든 기능을 적용하지 않도록 함.
    *
    * web.ignoring(): "이제부터 지정하는 경로는 무시해 주세요"
    * .requestMatchers(PathRequest.toH2Console()): H2 데이터베이스 콘솔로의 요청 무시
    * H2 콘솔은 개발할때 데이터베이스 쉽게 보는 도구인데, 시큐리티가 작동하면 접근이 막히니까 예외처리
    *
    * .requestMatchers("/static/**"): /static 폴더 아래에 있는 모든 정적 리소스에 대한 요청 무시
    * 이런 파일들은 굳이 로그인 여부랑 관계가 없으니까 무시
    */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers("/static/**"));
    }

    // 핵심 운영 규칙 설정
    /*
    * HTTP 요청에 대한 웹 기반 보안 구성
    *
    * http.authorizeHttpRequests(...): 지금부터 들어오는 HTTP 요청들에 대한 접근 권한 설정하겠따라는 의미
    *
    * .requestMatchers("/login", "/signup", "/user").permitAll():
    * /login, /signup, /user 경로로 오는 요청은 permitAll()
    * 누구나 접근 허용, 로그인 하지 않은 사람도 회원가입, 로그인 시도 가능
    *
    * .anyRequest().authenticated(): 위에서 허용한 경로 외의 나머지 모든 요청은 authenticated()
    * 인증된 사용자만 접근할 수 있음
    *
    * .formLogin(...): 로그인 방식은 HTML form을 이용한 로그인 사용
    *
    * .loginPage("/login"): 로그인 페이지 걍로는 /login.
    * 인증이 필요한 페이지에 비로그인 싱태면 이 페이지로 자동 이동함
    *
    * .defaultSuccessUrl("/articles"): 로그인이 성공하면 /articles 경로로 이동
    *
    * .logout(...): 로그 아웃에 대한 설정 시작의미
    *
    * .logoutSuccessUrl("/login"): 로그아웃에 성공하면 /login 페이지로 이동
    *
    * .invalidateHttpSession(true): 로그아웃 시, 사용자의 모든 세션 정보를 삭제
    *
    * .csrf(AbstractHttpConfigurer::disable): 웹 공격 막는 기능인데 일단 비활성화
    * 
    * .build(): 설정한 규칙들 종합해서 하나의 SecurityFilterChain 객체로 반환
    *
    * =========================================================================
    * HttpSecurity는 스프링 빈이 아님
    * 스프링 컨테이너가 관리하는게 아님
    * 걍 스프링 시큐리티 프레임워크가 제공하는 일회용 도구임
    * 스프링 시큐리티가 SecurityFilterChain이라는 빈을 만들기 위해서 그 과정에서만
    * 사용할 수 있도록 메소드 파라미터를 통해 특별히 제공해주는 설정용 빌더 객체
    *
    * 위에서 @EnableWebSecurity를 붙였기 때문에, 스프링 시큐리티는 @Bean으로 등록된
    * SecurityFilterChain을 찾고, 해당 빈을 만드는 메소드에 HttpSecurity를 주입해 줄 준비를 함
    *
    * 걍 HttpSecurity는 빈으로 만들어져 있는게 아니라 스프링 시큐리티랑 약속된 컨벤션 느낌
    * 프레임워크가 알아서 생성하고 전달해주는 특별한 객체
    */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/user")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    // 인증 관리자 관련 설정
    /*
    * `AuthenticationManager`: 이게 인증 관리자. 리턴해야할 객체임
    *
    * `AuthenticationManagerBuilder`: 이건 인증 관리자 만들고 설정하는 데 사용하는 도구
    * 여기에 사용자 정보(userService)는 어디서 어디서 나는지,
    * 비밀번호 비교(bCryptPasswordEncoder)는 뭐로 하는지 지시 사항 작성
    *
    * `http.getSharedObject(...)`: HttpSecurity 객체는 단순하게 보안 규칙만 설정하는거 말고도
    * 그 과정에서 필요한 여러 객체들을 공유하는 저장소 역할도 함 => SharedObject
    * getSharedObject가 공유 저장소에서 AuthenicationManagerBuilder 타입의 객체를 달라는 요청
    *
    * .userDetailsService(userService): 사용자 정보는 userService에게 물어보고 가져와라
    * userService는 데이터베이스에서 사용자 정보를 찾아오는 역할을 한다.
    *
    * .passwordEncoder(bCryptPasswordEncoder): 사용자가 입력한 비밀번호와
    * DB에 저장한 암호화된 비밀번호를 비교할 때는, bCryptPasswordEncoder라는 암호화 기술을 사용해서 비교
    * 절대 일반 텍스트로 비교 X
    */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http
            , BCryptPasswordEncoder bCryptPasswordEncoder
            , UserDetailService userDetailService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }

    // 비밀번호 암호화 하는 빈
    /*
    * 비밀번호를 안전하게 암호화하고 암호화된 비밀번호를 비교하는 데 사용할
    * BCryptPasswordEncoder라는 빈으로 등록
    *
    * 나중에 authenticationManager나 다른 곳에서 이 암호화 하는 빈을 가져다 쓸 수 있다
    */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
