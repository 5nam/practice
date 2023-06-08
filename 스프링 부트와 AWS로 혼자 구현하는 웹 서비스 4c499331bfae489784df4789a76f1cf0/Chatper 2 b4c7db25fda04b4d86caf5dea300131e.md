# Chatper 2

## 테스트 코드 소개

### TDD VS 단위 테스트

- TDD : 테스트가 주도하는 개발 → 테스트 코드를 먼저 작성하는 것부터 시작
    - 레드 그린 사이클
        1. 항상 실패하는 테스트를 먼저 작성하고 (Red)
        2. 테스트가 통과하는 프로덕션 코드를 작성하고 (Green)
        3. 테스트가 통과하면 프로덕션 코드를 리팩토링 (Refactor)
- 단위 테스트 : TDD 의 첫 단계인 기능 단위의 테스트 코드를 작성하는 것을 이야기

### @RestController VS @Controller

- Spring 에서 컨트롤러를 지정해주기 위한 어노테이션은 @Controller 와 @RestController 가 있음
- 전통적인 Spring MVC 의 컨트롤러인 @Controller 와 Restful 웹 서비스의 컨트롤러인 @RestController 의 **주요한 차이점은 HTTP Response Body 가 생성되는 방식**임
- @Controller 이해하기
    - Controller 로 View 반환하기
        
        ![Untitled](Chatper%202%20b4c7db25fda04b4d86caf5dea300131e/Untitled.png)
        
        1. Client 는 URI 형식으로 웹 서비스에 요청을 보냄
        2. DispatcherServlet 이 요청을 처리할 대상을 찾음
        3. HandlerAdapter 을 통해 요청을 Controller 로 위임
        4. Controller 는 요청을 처리한 후에 ViewName 반환
        5. DispatcherServlet 은 ViewResolver 를 통해 ViewName 에 해당하는 View 를 찾아 사용자에게 반환
        
        <aside>
        💡 Controller 가 반환한 뷰의 이름으로부터 View 를 렌더링하기 위해서는 ViewResolver 가 사용되며, ViewResolver 설정에 맞게 View 를 찾아 렌더링
        
        </aside>
        
    - Controller 로 Data 반환하기
        - 컨트롤러에서 데이터를 반환하기 위해 @ResponseBody 어노테이션을 활용해주어야 함
        - 이를 통해 컨트롤러도 **Json 형태로 데이터를 반환**할 수 있음
        
        ![Untitled](Chatper%202%20b4c7db25fda04b4d86caf5dea300131e/Untitled%201.png)
        
        1. Client 는 URI 형식으로 웹 서비스에 요청을 보낸다.
        2. DispatcherServlet 이 요청을 처리할 대상을 찾는다.
        3. HandlerAdapter 을 통해 요청을 Controller 로 위임한다.
        4. Controller 는 요청을 처리한 후에 객체를 반환한다.
        5. 반환되는 객체는 Json 으로 Serialize(직렬화) 되어 사용자에게 반환된다.
        - 컨트롤러를 통해 객체를 반환할 때, 일반적으로 ResponseEntity 로 감싸서 반환
            - 객체를 반환하기 위해서는 viewResolver 대신에 HttpMessageConverter 가 동작
            - HttpMessageConverter 에는 여러 Converter 가 등록되어 있고, 반환해야 하는 데이터에 따라 사용되는 Converter 가 달라짐
                
                ⇒ 단순 문자열 : StringHttpMessageConverter
                
                ⇒ 객체 : MappingJackson2HttpMessageConverter
                
            - Spring 은 클라이언트의 HTTP Accept 헤더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해 적합한 HttpMessageConverter 을 선택하여 처리
    
    <aside>
    💡 Dispatcher-Servlet(디스패처 서블릿)
    
    개념
    → 디스패처 서블릿의 dispatch 는 “보내다”라는 뜻을 가지고 있음.
    → **디스패처 서블릿은 HTTP 프로토콜로 들어오는 모든 요청을 가장 먼저 받아 적합한 컨트롤러에 이임해주는 프론트 컨트롤러(Front Controller)라고 정의**할 수 있음
    → 클라이언트로부터 어떤 요청이 오면 Tomcat 과 같은 서블릿 컨테이너가 요청을 받게 됨
    → 그리고 이 요청을 프론트 컨트롤러인 디스패처 서블릿이 가장 먼저 받게 됨. 그럼 디스패처 서블릿은 공통적인 작업을 먼저 처리한 후에 해당 요청을 처리해야 하는 컨트롤러를 찾아서 작업을 위임
    
    장점
    → dispatcher-servlet 이 해당 어플리케이션으로 들어오는 모든 요청을 핸들링해주고 공통 작업을 처리
    → 컨트롤러를 구현해두기만 하면 디스패처 서블릿이 알아서 적합한 컨트롤러로 위임해주는 구조
    
    정적 자원의 처리
    → Dispatcher Servlet 이 모든 요청을 처리하다보니 이미지나 HTML/CSS/JavaScript 등과 같은 정적 파일에 대한 요청마저 모두 가로채는 까닭에 정적자원(static resources)을 불러오지 못하는 상황도 발생
    → 해결 방법으로는 정적 자원 요청과 애플리케이션 요청을 분리하고, 애플리케이션 요청을 탐색하고 없으면 정적 자원 요청으로 처리하는 것이 있음
    
    출처 : [https://mangkyu.tistory.com/18](https://mangkyu.tistory.com/18)
    
    </aside>
    
    <aside>
    💡 HandlerAdapter
    
    개념
    → HandlerMapping 통해 찾은 컨트롤러를 직접 실행하는 기능을 수행
    → HandlerMapping 으로 찾은 오브젝트(컨트롤러)를 등록된 HandlerAdapter 들의 supports 메서드에 대입하여 지원 여부를 살피고, 부합할 경우 handler 메서드를 실행하여 ModelAndView 를 리턴!
    
    출처 : [https://show400035.tistory.com/132](https://show400035.tistory.com/132)
    
    </aside>
    
- @RestContoller 이해하기
    - @RestController 는 @Controller 에 @ResopnseBody 가 추가된 것
    - RestController 의 주용도는 Json 형태로 객체 데이터를 반환하는 것
    - 데이터를 응답으로 제공하는 REST API 를 개발할 때 주로 사용하며 객체를 ResponsdeEntity로 감싸서 반환함
    - 동작 과정 역시 @Controller 에 @ResponseBody 를 붙인 것과 동일

출처 : [https://mangkyu.tistory.com/49](https://mangkyu.tistory.com/49)

### @GetMapping

- HTTP Method 인 Get 요청을 받을 수 있는 API 를 만들어줌

### @ExtendWith(Spring.Extension.class)

- Spring TestContext Framework 를 Junit5 프로그래밍에 포함시킬 수 있음
- 확장을 선언적으로 등록하는데 사용됨. 인자로 확장할 Extension 을 추가하여 사용

### @WebMvcTest

- 여러 스프링 테스트 어노테이션 중, Web(Spring MVC)에 집중할 수 있는 어노테이션
- 선언할 경우 @Controller, @ControllerAdvice 등을 사용할 수 있음
- 단, @Service, @Component, @Repository 등은 사용 불가
- 여기서는 컨트롤러만 사용하므로 선언

### @Autowired

- 스프링이 관리하는 빈(Bean)을 주입받음

### private MockMvc mvc

- 웹 API 를 테스트할 때 사용
- 스프링 MVC 테스트의 시작점
- 이 클래스를 통해 HTTP, GET, POST 등에 대한 API 테스트 가능

### mvc.perform(get(”/hello”))

- MockMvc 를 통해 /hello 주소로 HTTP GET 요청을 함
- 체이닝이 지원되어 아래와 같이 여러 검증 기능을 이어서 선언할 수 있음

### .andExpect(status().isOk())

- mvc.perform 의 결과를 검증
- HTTP Header 의 Status 를 검증
- 우리가 흔히 알고 있는 200, 404, 500 등의 상태를 검증
- 여기선 OK 즉, 200 인지 아닌지 검증

### .andExpect(content().string(hello))

- mvc.perform 의 결과를 검증
- 응답 본문의 내용을 검증
- Controller 에서 “hello”를 리턴하기 때문에 이 값이 맞는지 검증

### 롬복의 @Getter

- 선언된 모든 필드의 get 메소드를 생성해 줌

### 롬복의 @RequiredArgsConstructor

- 선언된 모든 final 필드가 포함된 생성자를 생성해 줌
- final 이 없는 필드는 생성자에 포함되지 X

### asserThat

- assertj 라는 테스트 검증 라이브러리의 검증 메소드
- 검증하고 싶은 대상을 메소드 인자로 받음
- 메소드 체이닝이 지원되어 isEqualTo 같이 메소드를 이어서 사용 가능

### isEqualTo

- assertj 의 동등 비교 메소드
- assertThat 에 있는 값과 isEqualTo 의 값을 비교해서 같을 때만 성공