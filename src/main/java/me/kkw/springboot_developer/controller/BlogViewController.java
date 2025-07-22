package me.kkw.springboot_developer.controller;


import lombok.RequiredArgsConstructor;
import me.kkw.springboot_developer.domain.Article;
import me.kkw.springboot_developer.dto.ArticleListViewResponse;
import me.kkw.springboot_developer.dto.ArticleViewResponse;
import me.kkw.springboot_developer.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles") // 모든 글 조회
    public String getArticles(Model model) {
        /*
        * findAll 메소드는 List<Article>을 리턴함
        *
        * .stream()을 통해서 Article 리스트를 스트림으로 변환함
        * 스트림은 데이터의 흐름이라고 생각하면 됨
        * 리스트의 Article 객체들이 하나씩 흘러가는 파이프라인을 만드는 것이랑 같음
        * 스트림 뒤에는 .map이나 .filter 같은 데이터 가공 작업을 연쇄적으로 수행할 수 있음
        * 결과로는 Stream<Article>타입의 객체가 만들어짐
        * Article들이 파이프라인 위를 하나씩 흘러갈 준비를 한 상태
        *
        * .map()을 통해서 각 요소들 다른 형태의 요소로 mapping함
        * 인자로는 mapping을 수행할 함수를 작성해줌.
        *
        * ArticleListViewResponse::new는 "메소드 레퍼런스" 라는 자바 문법
        * :: 기호는 특정 메소르르 직접 가리키는 참조 역할을 함
        * new는 생성자를 의미
        * 따라서 ArticleListViewResponse::new는 ArticleListViweResponse클래스의 생성자를 가리킨다.4
        * 이건 사실 람다 표현식인 article -> ArticleListViewResponse(article)이랑 같음
        *
        * .toList()는 스트림의 최종 단계에서 사용한다
        * 스트림을 따라 들어온 모든 요소들을 모아서 다시 List로 만들어준다
        * 결과물로 List<ArticleListViewResponse> 타입의 객체가 만들어진다.
        */
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new).toList();

        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}") // 글 조회
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);

        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article") // 글 수정과 생성
    /*
    * @RequestParam은 쿼리 파라미터 값을 가져오라는 것
    * URL 주소 뒤에 ?로 시작하는 쿼리 문자열에 포함된 파라미터값을
    * 컨트롤러 메소드의 파라미터 변수에 넣어주는 역할을 한다.
    *
    * 쿼리 파라미터는 URL에서 ? 뒤에 key=value 형태로 붙는 값들이다
    * 여러개면 &로 연결된다
    * 정렬이나 검색 필터링 페이지네이션 등 부가적인 옵션을 전달할 때 많이 사용
    *
    * 1. 글 수정 (/new-article?id=###)
    * 사용자가 수정 버튼을 누르면 클라이언트는 수정할 id를
    * 쿼리 파라미터에 담아서 요청을 보낸다.
    * 스프링은 URL에서 id=### 부분을 발견한다.
    * @RequestParam Long id 부분을 보고 URL의 id 파라미터 값을 찾아서
    * Long 타입의 id 변수에 넣는다.
    * 따라서 메소드의 id 변수는 ###L이라는 값을 가진다.
    * 이후 로직에서 이 id 값을 사용해서 기존 글의 정보를 찾아와 model에 담아 뷰로 전달한다.
    *
    * 2. 새 글 생성(new-article)
    * 사용자가 글 쓰기 버튼을 누르면 클라이언트는 id 없이
    * 그냥 /new-article 경로로 요청을 보낸다
    * URLd에 id쿼리 파라미터가 없다.
    * 이때 @RequestParam(required=false) 옵션이 있는데
    * 이 파라미터는 필수가 아니라는 의미이다
    * 만약 URL에 없으면 그냥 null값을 넣는다.
    * 이 옵션이 없으면 기본값인 required = true 가 지정되고
    * 스프링은 id 파라미터를 찾지 못해서 에러가 발생한다
    * 이 뒤로는 id가 null인 것을 보고, 이건 새글 작성이라고 스프링은 판단한다.
    */
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if(id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}
