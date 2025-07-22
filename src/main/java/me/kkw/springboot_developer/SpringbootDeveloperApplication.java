package me.kkw.springboot_developer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
* @EnableJpaAuditing
* 이 애노테이션은 JPA의 Auditing 기능을 전체적으로 활성화하는 스위치와 같은 역할
* 이 애노테이션을 메인 어플리케이션 클래스나 설정 클래스에 추가하면 Spring Data JPA는
* 어플리케이션 내에서 Auditing 기능을 사용할 준비를 한다.
*
* 다음으로 @EntityListeners(AuditingEntityListener.class)가 붙은 엔티티를 찾아낸다.
* 그리고 해당 엔티티에 생성이나 수정같은 이벤트가 발생하면 AuditingEntityListener가 동작해서
* @CreatedDate나 @LastModifiedDate 같은 애노테이션이 붙은 필드에 현재 시간을 자동으로 채운다.
*
* createdAt이나 updatedAt 같은 필드를 매번 서비스 로직에서 LocalDateTime.now() 등으로 직접 설정해 줄 필요가 없다.
*/
@EnableJpaAuditing
@SpringBootApplication
public class SpringbootDeveloperApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringbootDeveloperApplication.class, args);
	}

}
