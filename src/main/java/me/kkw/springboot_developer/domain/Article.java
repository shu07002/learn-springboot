package me.kkw.springboot_developer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*
* 데이터베이스 테이블과 직접 매핑되는 핵심 객체들
* 데이터베이스의 article 테이블 구조를 그대로 자바 클래스로 표햔한 것.
* 여기서는 @Entity 애노테이션이 붙는다.
* 실제 데이터 원본 그 자체다
*/

@EntityListeners(AuditingEntityListener.class)
@Entity // 엔티티로 지정
@Getter // 클래스의 모든 필드에 대한 접근자 메소드를 만듦
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Args 없는 생성자 = 기본 생성자 / 타입은 protected로
public class Article {

    @Id // 어떤 attr을 기본키로 설정할 때 사용하는 애너테이션, id를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 속성 전략/ 새로 생길때마다 1씩 증가
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    /*
    * <일반 객체 생성>
        new Article("~~","@@@");
    *
    * <빌더 패턴으로 객체 생성>
        Article.builder().title("~~").content("@@@").build();
        *
    확실히 빌더 패턴이 직관적임.
    */

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}



//public class Article {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", updatable = false)
//    private Long id;
//
//    @Column(name = "title", nullable = false)
//    private String title;
//
//    @Column(name = "content", nullable = false)
//    private String content;
//
//    @Builder
//    public Article(String title, String content) {
//        this.title = title;
//        this.content = content;
//    }
//
//    public Article() {}
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//
//    public String getContent() {
//        return content;
//    }
//}
