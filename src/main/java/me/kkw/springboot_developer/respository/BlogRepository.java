package me.kkw.springboot_developer.respository;

import me.kkw.springboot_developer.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/*
* 데이터베이스와 소통을 담당함.
* 실제 데이터베이스에 접근해서 데이터를 저장하고 조회하고 수정하고 삭제하고...
* JAP라는 기술로 개발자가 SQL 쿼리문을 직접 작성하지 않아도 되게끔 도와준다.
* JpaRepository를 상속받는 것만으로도 save(), findAll(), findById() 같은 기본적인 데이터 처리 메소드를 자동으로 사용 가능
*
*/
public interface BlogRepository extends JpaRepository<Article, Long> {
}
