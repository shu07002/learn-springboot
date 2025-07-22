package me.kkw.springboot_developer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.Article;
import me.kkw.springboot_developer.dto.AddArticleRequest;
import me.kkw.springboot_developer.dto.UpdateArticleRequest;
import me.kkw.springboot_developer.respository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* 핵심적인 비즈니스 로직 처리 담당
* 애플리케이션의 실질적인 기능이 구현되는 곳
* 컨트롤러로부터 받은 데이터를 가지고 뭘 할지 결정함. "블로그 글 저장", "모든 글 불러오기", ...
* 위 작업들을 디비에 저장하고나 조회하기위해서 레포지토리 계층에 요청을 보낸다. 그래서 레포지토리 필드가 필요함,
* 여러 개의 작업을 묶어서 하나의 트랜잭션으로 처리함
*/

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    /*
    * @Transactional이 붙은 메소드 안에서 어떤 두 순차적인 작업 1과 2가 수행될 때
    * 1번 작업이 성공하고 2번 작업에서 에러가 발생하면 데이터베이스는 이 트랜잭션이 실패했다고 판단한다.
    * 따라서 이미 성공했던 1번 작업을 취소한다. 이 과정을 롤백이라고 한다.
    * 결과적으로 데이터의 일관성이 유지된다.
    * 트랜잭션 내부에 존재하는 모든 작업이 성공적으로 끝나야만 최종적으로 데이터 베이스에 결과를 저장한다.
    *
    * 또한 이 애노테이션입 붙으면 스프링은 데이터베이스 연결에 대한 트랜잭션을 시작한다.
    * JPA가 데이터베이스에서 엔티티를 조회하면 JPA는 이 엔티티의 최초 상태를 기억해서 저장한다.
    * 메소드가 예외 없이 끝나면 트랸잭션을 데이터베이스에 저장(커밋)하려고 한다.
    * 이때 커밋 직전에 JPA는 자신이 가지고 있는 엔티티의 최조 상태와 현재의 엔티티 상태를 비교한다.
    * 만약 변경된 부분이 있으면(Dirty) JPA가 알아서 UPDATE SQL Query를 생성해서 데이터베이스에 보낸다.
    * 이게 변경 감지(Dirty Checking)
    * 이 UPDATE 쿼리꺼지 성공적으로 실행되면 트랜잭션이 최종적으로 커밋되고 데이터베이스에 영구적으로 결과가 저장된다.
    * 따라서 명시적으로 repository.save() 이런 코드를 호출하지 않아도 데이터베이스에 엔티티의 변경 상태가 잘 업데이트 된다.
    */
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
