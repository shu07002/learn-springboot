package me.kkw.springboot_developer.controller;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;


/*
* @RestController와 @Controller의 차이점
*
* @Controller는 View를 찾아서 보여줌
* 사용자가 웹사이트에 접속했을 때 서버에서 완성된 HTML 페이지를 만들어서 반환함.
* 1. 컨트롤러의 메소드가 문자열을 반환하면 스프링은 이걸 View의 이름으로 해석
* 2. 스프링의 ViewResolver라는 설정에 따라서 이 이름에 해당하는 템플릿 파일을 찾는다
* 3. Model 객체에 담아둔 데이터를 이 HTML 파일에 채워서 최종 HTML ㅠㅔ이지를 환성
*
* @RestController는 Data를 직접 줌
* REST API 개발할 때 사용함
* 이 애노테이션은 사실 @Controller와 @RespomseBody를 합친 것
* @ResponseBody의 역할은 메소드의 반환값을 뷰의 이름으로 해석하지 않도록 함
* 그 리턴 값 자체를HTTP 응답 바디 (Response Body)에 직접 써넣으라는 지시
* 따라서 자바 객체를 반환하면, 스프링은 Jackson 같은 라이브러리를 사용해서
* 객체를 JSON 문자열로 변환해서 응답 본문에 담아 보냄
*/
@Controller
public class ExampleController {

    @GetMapping("/thymeleaf/example")
    /*
    * Model 객체
    * 컨트롤러에서 View로 데이터를 전달하는 다리 역할
    * 데이터를 운반하는 상자같은 것
    * 이 데이터는 주로 타임리프 같은 템플릿 파일에서 사용 가능
    * @RestController처럼 데이터 자체를 반환하는 녀석은 필요 없고
    * 여기서 @Controller 처럼 문자열을 반환하는 녀석에게 사용
    * 컨트롤러가 반환한 뷰에서는 , Model에 담겨온 데이터에 접근할 수 있음
    * addAttribute 속성으로 이 모델 객체에 속성 추가
    */
    public String thymeleafExample(Model model) {
        Person examplePerson = new Person();

        examplePerson.setId(1L);
        examplePerson.setName("김경우");
        examplePerson.setAge(25);
        examplePerson.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", examplePerson);
        model.addAttribute("today", LocalDateTime.now());

        // example.html 이라는 이름으로 뷰를 반환
        return "example";
    }

    @Getter
    @Setter
    class Person {
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}
