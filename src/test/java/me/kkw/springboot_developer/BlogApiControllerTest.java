package me.kkw.springboot_developer;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kkw.springboot_developer.domain.Article;
import me.kkw.springboot_developer.dto.AddArticleRequest;
import me.kkw.springboot_developer.dto.UpdateArticleRequest;
import me.kkw.springboot_developer.respository.BlogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogApiControllerTest {

    /*
    * 컨트롤러 API를 테스트하려고 가쩌 MVC 환경 사용
    * 실제 서버 안띄우고 HTTP 요청 보내기 가능
    * @SpringBootTest가 내가 이전에 만들어 놓은 모든 컴포넌트를 메모리에 올림
    * @AutoConfigureMockMvc가 MockMvc라는 빈을 만들어서 스프링 컨텍스트에 추가함
    * 그래서 Autowired 하면 이미 만들어진 스프링 빈을 찾아서 여기에 자동으로 주입해 줌
    */
    @Autowired
    protected MockMvc mockMvc;

    /*
    * 잭슨 라이브러리에 있는 클래스
    * 자바 객체를 문자열로 바꾸거나 문자열을 자바 객체로 바꾸기 위해 사용
    * HTTP 요청 본문에 실을 JSON을 문자열로 만들겨고 사용
    */
    @Autowired
    protected ObjectMapper objectMapper;

    /*
    * 이건 스프링의 애플리케이션 컨텍스트 그 자체임 (DI 컨테이너)
    * 이 녀석이 모든 빈을 관리함
    * @BeforeEach 부분 보면 여기서 MockMvc 설정할 때, 이 컨텍스트를 사용하고 있음
    * 해단 라인을 통해서 테스트용 MockMvc가 실제 애플리케이션의 모든 설정과 빈들을 인지함
    */
    @Autowired
    private WebApplicationContext context;

    /*
    * 디비의 Article 테이블에 접근하려고 선언한 JPA 레포지토리
    * POST나 GET이 잘 되었는지 확인하려면 디비에 접근해야함. 그래서 사용
    * 디비는 각 테스트 하기 전에 항상 깨끗하게 비움
    * Autowired로 서비스 계층에서 사용하는 레포지토리랑 동일한 것을 주입받아서 사용함
    */
    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("addArticle: 블로그 글 추가에 성공")
    public void addArticle() throws Exception{
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        /*
        * ResultActions는 mockMvc.perform(..) 메소드가 반환하는 객체.
        * 요청의 실형 결과에 대한 행동들을 담고 있음
        * POST나 PUT 처럼 요청 바디에 데이터를 실어 보낼 때 사용함
        * 여기서는 JSON 형태로 만들어서 보내기 때문에 contentType을 APPLICATION_JSON으로 표시함
        */
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        /*
        * 위에서 반환된 객체에 대해서 후속 작업을 이어감
        * result.andExpect(status().isCreated());
        * result에 대한 상태 코드가 isCreated인지를 expect함
        */
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        Assertions.assertThat(articles.size()).isEqualTo(1);
        Assertions.assertThat(articles.getFirst().getTitle()).isEqualTo(title);
        Assertions.assertThat(articles.getFirst().getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    public void findAllArticles() throws Exception {
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        /*
        * accept(MediaType.APPLICATION_JSON)은 "나는 JSON 형식을 받고 싶다"는 것을 의미함
        * GET 처럼 서버로부터 데이터를 받아올 때, 특정 형식의 응답을 원한다고 명시할 때 사용함
        * 서버는 이 헤더를 보고 클라이언트가 원하는 형식으로 데이터를 가공해서 보낼 수 있음.
        * 여기서는 JSON 응답을 기대하기 때문에 accept에 APPLICATION_JSON
        */
        final ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        /*
        * jsonPath는 JSON 구조에서 특정 데이터에 접근하기 위한 경로 표현식임
        * $: JSON 문서의 최상위 루트 객체 의미함 모든jsonPath 표현식은 $에서 시작함
        * $.필드명: 루트가 객체일 때, 그 객체의 특정 필드에 접근한다.
        * $[인덱스]: 루트가 배열일때특정 순서의 요소에 접근한다. 당연히 인덱스는 0부터 시작
        */
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @Test
    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    public void findArticle() throws Exception{
        final String url = "/api/articles/{id}";
        final String title = "제목";
        final String content = "내용";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final ResultActions result = mockMvc
                .perform(get(url, savedArticle.getId())
                        .accept(MediaType.APPLICATION_JSON));

        /*
         * jsonPath는 JSON 구조에서 특정 데이터에 접근하기 위한 경로 표현식임
         * $: JSON 문서의 최상위 루트 객체 의미함 모든jsonPath 표현식은 $에서 시작함
         * $.필드명: 루트가 객체일 때, 그 객체의 특정 필드에 접근한다.
         * $[인덱스]: 루트가 배열일때특정 순서의 요소에 접근한다. 당연히 인덱스는 0부터 시작
         */
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @Test
    @DisplayName("deleteArticle: 블로그 글 삭제 성공한다.")
    public void deleteArticle() throws Exception{

        // given
        final String url = "/api/articles/{id}";
        final String title = "제목";
        final String content = "내용";

        final Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when
        ResultActions result = mockMvc.perform(delete(url, savedArticle.getId()));

        result
                .andExpect(status().isOk());

        // then
        List<Article> articles = blogRepository.findAll();

        Assertions.assertThat(articles).isEmpty();
    }

    @Test
    @DisplayName("putArticle: 블로그 글수정에 성공한다.")
    public void putArticle() throws Exception{

        // given
        final String url = "/api/articles/{id}";
        final String title = "제목";
        final String content = "내용";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "title";
        final String newContent = "content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));


        result
                .andExpect(status().isOk());

        // then
        /*
        * 여기 마지막에 있는 get은 "확실하게 있으니까 꺼내서 내놔" 라는 의미임
        * 사실 findById는 Article 타입을 리턴하는게 아니라 Optional<Article> 타입을 리턴함
        * Optional 은 포장지라고 생각하면 됨.
        * 만약에 잘 찾았다면 .get()으로 포장지를 벗기고 Article 객체를 얻을 수 있음
        * 만약에 못찾았는데 .get() 이래버리면 큰일나는거 => NoSuchElementException 에러 펑
        *
        */
        Article article = blogRepository.findById(savedArticle.getId())
                .orElseThrow(()->new IllegalArgumentException("not found " + savedArticle.getId()));

        Assertions.assertThat(article.getTitle()).isEqualTo(newTitle);
        Assertions.assertThat(article.getContent()).isEqualTo(newContent);
    }
}
