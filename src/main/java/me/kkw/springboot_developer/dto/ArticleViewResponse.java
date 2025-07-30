package me.kkw.springboot_developer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.kkw.springboot_developer.domain.Article;

import java.time.LocalDateTime;

/*
* 이 DTO는 글 상세 화면을 위한 DTO
* GET /articles/{id} -> article.html
* 하나의 글에 대한 모든 상세 정보를 보여준다.
* 제목, 내용뿐만 아니라 작성자, 작성 시간, 수정 시간, 댓글 등 모든 정보가 필요함
*
* 기술적으로 ArticleResponse 걍 사용할 수 있는데 안쓰고 새롭게 만든 이유는
* ArticleResponse의 목적은 API 계층을 위해 만들어진 DTO기 때문임
* 하지만 이 녀석은 View 계층을 위해 만들어진 것
*
* 여기서 final 키워드를 사용하지 않은 이유는 기본 생성자 때문
* 자바 문법상 기본 생성자는 필드 값을 외부에서 받아올 방법이 겂이 때문에
* final 필드를 초기화할 수 없다.
* 따라서 final 필드가 있으면 이는 기본 생성자랑 양립이 안된다.
*
* 여기서 기본 생성자가 필요한 이유는 BlogViewController의 글 수정 생성 부분에서
* id가 null이라면 새 글 생성인 상황이고 이때는 모든 필드 값이 null인 상태로
* 폼이 만들어져야 하기 때문에 기본 생성자로 객체 인스턴스를 생성한다.
* (수정과 삭제가 너무 비슷해서 같안 DTO 사용하려고 시도중인 상황이라고 이해하면 더 와닿음)
*
*/

@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
    }
}
