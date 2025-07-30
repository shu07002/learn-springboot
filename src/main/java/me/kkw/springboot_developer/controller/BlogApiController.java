package me.kkw.springboot_developer.controller;

import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.Article;
import me.kkw.springboot_developer.dto.AddArticleRequest;
import me.kkw.springboot_developer.dto.ArticleResponse;
import me.kkw.springboot_developer.dto.UpdateArticleRequest;
import me.kkw.springboot_developer.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


/*
* 외부로부터 오는 HTTP 요청을 가장 먼저 받는 곳
* GetMapping같은 어노테이션 보고 특정 URL 요청을 어떤 메소드가 처리할 지 결정한다.
* 요청에 담겨온 데이터를 받아서 뒤에 있는 서비스 계층에 전달함. 그래서 여기서 blogService 필드를 선언했음.
* 서비스가 처리를 끝내고 돌려준 데이터를 사용자에게 보여줄 최종 형태로 가공해서 응답한다.
*/


@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/api/articles")
    // ResponseEntity: 스프링이 제공하는 HTTP Response 응답 전체를 표현하는 클래스
    // <Article>: 내가 처리할 리스폰의 바디가 Article 타입이라는 의미
    // @RequestBody AddArticleRequest request: HTTP 요청의 바디에 있는 제이슨을 지정한 타입(AddArticleRequest)의 자바 객체로 변환
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());

        // ResponseEntity.status(HttpStatus.CREATED): 상태 코드를 201(CREATED)로 설정
        // .body(savedArticle): 요청에 대한 답변의 바디에 새로 생성한 객체를 넣어서 보내줌
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> putArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updatedArticle);
    }
}
