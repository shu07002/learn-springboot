package me.kkw.springboot_developer.dto;

import lombok.Getter;
import me.kkw.springboot_developer.domain.Article;

/*
* 글 목록 화면을 위한 DTO
* GET /articles -> articleList.html
* 여러 개의 글을 간략하게 보여준다
* 보통 글의 전체 내용보다 제모과 작성자, 아이디 정도만 보여준다
* 목록화면에서 꼭 필요한 최소한의 데이터만 담아서 보여준다.
* 
* 기술적으로 ArticleResponse 걍 사용할 수 있는데 안쓰고 새롭게 만든 이유는
* ArticleResponse의 목적은 API 계층을 위해 만들어진 DTO기 때문임
* 하지만 이 녀석은 View 계층을 위해 만들어진 것
*
* 여기 final 키워드가 붙은 이유는 데이터베이스에서 조회한
* Article의 정보를 담아서 단순히 View에 보여주기 위한 목적이기 때문임
* => 불변성
* 요기서 final 키워드르 이 객체의 데이터가 절대 변하지 않는다는 것을 보장
* final 키워드는 그리고 항상 생성자에서 초기화를 해주어야 함.
* 그래서 항상 모든 값을 가진 완전한 상태로만 생성될 수 있음
*/

@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
