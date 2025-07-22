package me.kkw.springboot_developer.dto;

import lombok.Getter;
import me.kkw.springboot_developer.domain.Article;

/*
 * Data Transfer Object
 * 계층 간의 데이터 전송을 위한 객체임
 * 각 계층(특히 컨트롤러)가 필요로 하는 데이터만을 담아서 주고받기 위해 만든 맞춤형 그릇
 * Article 도메인 에는 id나 createdAt 같은 사용자가 직접 입력하지 않아야 하는 필드가 존재함
 * AddArticleRequest DTO에는 title, content만 담아서 사용자가 불필요한 정보를 보내는 것을 원천 차단함
 * 화면마다 필요한 데이터 형식이 다를 수 있음. 근데 화면에 보여줄 데이터만 깔끔하게 가공해서 보내면 불필요한 데이터 전송을 막을 수 있음
 */


@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    // 생성자의 파라미터를 Article 객체로
    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
