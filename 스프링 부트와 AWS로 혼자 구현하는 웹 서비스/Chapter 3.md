# Chapter 3

## JPA

- 객체를 관계형 데이터 베이스에서 관리하는 것이 중요한데, 데이터 베이스와 객체지향의 패러다임 불일치 문제를 해결해준다.
- 개발자는 객체지향적으로 프로그래밍을 하고, JPA 가 이를 관계형 데이터베이스에 맞게 SQL 을 대신 생성해서 실행
- SQL 에 종속적인 개발을 하지 않아도 됨

## Spring Data JPA

- 구현체들을 좀 더 쉽게 사용하고자 추상화시킨 Spring Data JPA 라는 모듈을 이용하여 JPA 기술을 다룸
    
    JPA ← Hibernate ← Spring Data JPA
    
- 한 단계 더 감싸놓은 Spring Data JPA 가 등장한 이유는 크게 두 가지가 있음
    1. 구현체 교체의 용이성
        
        → Hibernate 외에 다른 구현체로 쉽게 교체하기 위함
        
    2. 저장속 교체의 용이성
        
        → 관계형 데이터베이스 외에 다른 저장소로 쉽게 교체하기 위함
        

## 요구사항 분석

### 게시판 기능

- 게시글 조회
- 게시글 등록
- 게시글 수정
- 게시글 삭제

### 회원 기능

- 구글/네이버 로그인
- 로그인한 사용자 글 작성 권한
- 본인 작성 글에 대한 권한 권리

![Untitled](Chapter%203/Untitled.png)

![Untitled](Chapter%203/Untitled%201.png)

## Posts 클래스

### @Entity

- 테이블과 링크될 클래스임을 나타냄
- 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭함
    - [SalesManager.java](http://SalesManager.java) → sales_manager table

### @Id

- 해당 테이블의 PK 필드를 나타냄

### @GeneratedValue

- PK 의 생성 규칙을 나타냄
- 스프링 부트 2.0 에서 GenerationType.IDENTITY 옵션을 추가해야 auto_increment 가 됨

### @Column

- 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 됨
- 사용하는 이유는, 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용
    
    → 문자열의 경우 VARCHAR(255)가 기본값인데, 사이즈를 500으로 늘리고 싶거나(ex. title), 타입을 TEXT 로 변경하고 싶거나(ex. content) 등의 경우에 사용됨
    

### @NoArgsConstructor

- 기본 생성자 자동 추가
- public Posts() {} 와 같은 효과

### @Getter

- 클래스 내 모든 필드의 Getter 메소드를 자동생성

### @Builder

- 해당 클래스의 빌더 패턴 클래스를 생성
- 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함

`Entity 클래스에서는 절대 Setter 메소드를 만들지 않음`

⇒ 그럼 Setter 가 없는 이 상황에서 어떻게 값을 채워 DB 에 삽입해야 할까?

⇒ 기본적인 구조는 생성자를 통해 최종값을 채운 후 DB 에 삽입하는 것이고, 값 변경이 필요한 경우 해당 이벤트에 맞는 public 메소드를 호출하여 변경하는 것을 전제로 함

⇒ 생성자 대신에 @Builder 를 통해 제공되는 빌더 클래스를 사용할 수도 있음

### JpaRepository 생성

- 보통 ibatis 나 MyBatis 등에서 Dao 라고 불리는 DB Layer 접근자임
- JPA 에선 Repository 라고 부르며 인터페이스로 생성
- 인터페이스를 생성 후, JpaRepository<Entity 클래스, PK 타입>를 상속하면 기본적이 CRUD 메소드가 자동으로 생성됨
- @Repository 를 추가할 필요도 없음.
    - **`주의해야하는 점이 Entity 클래스와 기본 Entity Repository 는 함께 위치해야 함`**

### @AfterEach

- Junit 에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정
- 보통은 배포 전 전체 테스트를 수행할 때 테스트간 데이터 침범을 막기 위해 사용
- 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 H2에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있음

## 등록/수정/조회 API 만들기

- API 를 만들기 위해 총 3개의 클래스가 필요
    - Request 데이터를 받을 Dto
    - API 요청을 받을 Controller
    - 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service
- Service 에서 비즈니스 로직을 처리해야 하는 것이 아님.
    - Service 는 트랜잭션, 도메인 간 순서 보장의 역할만 함
- Spring 웹 계층
    
    ![Untitled](Chapter%203/Untitled%202.png)
    
    - Web Layer
        
        → 흔히 사용하는 컨트롤러와 JSP/Freemarker 등의 뷰 템플릿 영역
        
        → 이외에도 필터(@Filter), 인터셉터, 컨트롤러 어드바이스(@ControllerAdvice) 등 외부 요청과 응답에 대한 전반적인 영역을 이야기 함
        
    - Service Layer
        
        → @Service 에 사용되는 서비스 영역
        
        → 일반적으로 Controller 와 Dao 의 중간 영역에서 사용됨
        
        → @Transactional 이 사용되어야 하는 영역이기도 함
        
    - Repository Layer
        
        → Database 와 같이 데이터 저장소에 접근하는 영역
        
        → Dao(Data Access Object) 로 이해하면 됨
        
    - Dtos
        
        → Dto 는 계층 간에 데이터 교환을 위한 객체를 이야기 하며 Dtos 는 이들의 영역을 이야기 함
        
        - DAO
            
            • **`DAO(Data Access Object)`** 는 데이터베이스의 data에 접근하기 위한 객체. **DataBase에 접근 하기 위한 로직 & 비지니스 로직**을 분리하기 위해 사용합니다.
            
        - **`DTO(Data Transfer Object)`** 는 계층 간 데이터 교환을 하기 위해 사용하는 객체로, DTO는 로직을 가지지 않는 순수한 데이터 객체(getter & setter 만 가진 클래스)입니다.
        - 유저가 입력한 데이터를 DB에 넣는 과정을 보겠습니다.
            - 유저가 자신의 브라우저에서 데이터를 입력하여 form에 있는 데이터를 DTO에 넣어서 전송합니다.
            - 해당 DTO를 받은 서버가 DAO를 이용하여 데이터베이스로 데이터를 집어넣습니다.
    - Domain Model
        
        → 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인 모델이라고 함
        
        → 이를테면 택시 앱이라고 하면 배차, 탑승, 요금 등이 모두 도메인이 될 수 있음
        
        → **@Entity 가 사용된 영역 역시 도메인 모델이라고 이해**하면 됨
        
        → 다만, 무조건 데이터베이스의 테이블과 관계가 있어야 하는 것은 아님
        
        → VO 처럼 값 객체들도 이 영역에 해당하기 때문
        
- Web(Controller), Service, Repository, Dto, Dmain 이 5가지 레이어에 **비지니스 처리를 담당해야 할 곳**은?
    
    ⇒ 바로 **Domain**!
    
- 기존에 서비스로 처리하던 방식을 트랜잭션 스크립트라고 함.
    
    ```java
    	@Transactional
    public Order cancelOrder(int orderId) {
    	// 1) 데이터베이스로부터 주문정보(orders), 결제정보(Billing), 배송정보(Delivery) 조회
    	OrderDto order = orderDao.selectOrders(orderId);
    	BillingDto billing = billingDao.selectBilling(orderId);
    	DeliveryDto delivery = deliveryDao.selectDelivery(orderId);
    	
    	// 2) 배송 취소를 해야하는지 확인
    	String deliveryStatus = delivery.getStatus();
    
    	// 3) 배송중이라면, 배송 취소로 변경
    	if("IN_PROGRESS".equals(deliveryStatus)) {
    		delivery.setStatus("CANCEL");
    		deliveryDao.update(delivery);
    	}
    	
    	// 4) 각 테이블에 취소 상태 update
    	order.setStatus("CANCEL");
    	orderDao.update(order);
    	
    	billing.setStatus("CANCEL");
    	deliveryDao.update(billing);
    
    	return order;
    }
    ```
    
    → 모든 로직이 서비스 클래스 내부에서 처리됨
    
    → 서비스 계층이 무의미하며, 객체란 단순히 데이터 덩어리 역할만 하게 됨
    
- 도메인 모델에서 처리할 경우
    
    ```java
    @Transactional
    public Order cancelOrder(int orderId) {
    	// 1) 데이터베이스로부터 주문정보(orders), 결제정보(Billing), 배송정보(Delivery) 조회
    	Orders order = ordersRepository.findById(orderId);
    	Billing billing = billingReopository.findByOrderId(orderId);
    	Delivery delivery = deliveryReposiotry.findByOrderId(orderId);
    
    	// 2) 배송 취소를 해야하는지 확인 - 3) 배송중이라면, 배송 취소로 변경
    	delivery.cancel();
    
    	// 4) 각 테이블에 취소 상태 update
    	order.cancel();
    	billing.cancel();
    
    	return order;
    }
    ```
    
    → order, billing, delivery 각자 취소 이벤트 처리를 하며, 서비스 메소드는 트랜잭션과 도메인 간의 순서만 보장해 줌
    
- 등록/수정/삭제 기능 만들기
    - PostsApiController
        
        ```java
        @RepuiredArgsConstructor
        @RestController
        public class PostsApiController {
        	private final PostsService postsService;
        	
        	@PostMapping("/api/v1/posts")
        	public Long save(@RequestBody PostsSaveRequestDto requestsDto) {
        		return postsService.save(requestDto);
        	}
        }
        ```
        
    - PostsService
        
        ```java
        @RequiredArgsConstructor
        @Service
        public class PostsService {
        	private final PostsRepository postsRepository;
        
        	@Transactional
        	public Long save(PostsSaveRequestDto requestDto) {
        		return postsRepository.save(requestDto.toEntity()).getId();
        	}
        }
        ```
        
        → 스프링에서 Bean 을 주입받는 방식이 3가지가 있음
        
        1. @Autowired
        2. setter
        3. 생성자
        
        → 이 중 가장 권장하는 방식이 생성자로 주입받는 방식
        
        → 이 코드에서 @RequieredArgsConstructor 에 해당함
        
        : final 이 선언된 모든 필드를 인자값으로 하는 생성자를 롬복의 @RequiredArgsConstructor 가 대신 생성해준 것
        
        : 생성자를 직접 안 쓰고 롬복 어노테이션을 사용한 이유는 해당 클래스의 의존성 관계가 변경될 때마다 생성자 코드를 계속해서 수정하는 번거로움을 해결하기 위해
        
    - Controller 와 Service 에서 사용한 Dao 클래스 생성하기
    - PostsSaveRequestDto
        
        → Entity 클래스와 유사함에도 Dto 클래슬 추가로 생성
        
        → Entity 클래스를 Request/Reponse 클래스로 사용해서는 안됨
        
        ⇒ Entity 클래스는 데이터베이스와 맞닿은 핵심 클래스
        
        ⇒ 화면 변경은 아주 사소한 기능 변경인데, 이를 위해 테이블과 연결된 Entity 클래스를 변경하는 것은 너무 큰 변경
        
        ⇒ Entity 클래스를 변경하는 것은 여러 클래스에 영향을 끼치지만, Request 와 Reponse 용 Dto 는 View 를 위한 클래스라 자주 변경이 필요함
        
        → View Layer 와 DB Layer 의 역할 분리를 철저하게 하는 게 좋음
        
        ⇒ 실제로 Controller 에서 결과값으로 여러 테이블을 조인해서 줘야 할 경우가 빈번하므로 Entity 클래스 만으로 표현하기가 어려운 경우가 많음
        
        ⭐ 꼭 Entity 클래스와 Controller 에서 쓸 Dto 는 분리해서 사용해야 함
        
    - PostsService
        - 여기서 update 기능에 데이터베이스에 쿼리를 날리는 부분이 없음
            
            ⇒ 이게 가능한 이유는 JPA 의 영속성 컨텍스트 때문
            
            ⇒ 영속성 컨텍스트란, 엔티티를 영구저장하는 환경임
            
            ⇒ JPA 의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있느냐 아니냐로 갈림
            
            ⇒ JPA 의 엔티티 매니져가 활성화된 상태로 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상태
            
            ⇒ 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영함
            

## JPA Auditing 으로 생성시간/수정시간 자동화

- 보통 Entity 는 해당 데이터의 생성시간과 수정시간을 포함
- DB 삽입하기 전, 갱신하기 전에 날짜 데이터를 등록/수정하는 코드가 여기저기 들어가게 됨
- 이런 단순하고 반복적인 코드가 지속적으로 포함되어야 한다면 코드가 매우 지저분해질 것.
- 이런 문제를 해결하고자 JPA Auditing 사용

### LocalDate 사용

```jsx
@Getter
@MappedSuperclass
@EntityListeners(AuditionEntityListenr.class)
public abstract class BaseTimeEtity {
	@CreateDate
	private LocalDateTime createdDate;
	
	@LastModifiedDate
	private LocalDateTime modifiedDate;
}
```

→ BaseTimeEntity 클래스는 모든 Entity 의 상위 클래스가 되어 Entity 들의 createdDate, modifiedDate 를 자동으로 관리하는 역할

- @MappedSuperclass
    - JPA Entity 클래스들이 BaseTimeEntity 를 상속할 경우 필드들도 칼럼으로 인식하게 함
- @EntityListeners(AuditingEntityListener.class)
    - BaseTimeEntity 클래스에 Auditing 기능을 포함시킴
- @CreatedDate : Entity 가 생성되어 저장될 때 시간이 자동 저장됨
- @LastModifiedDate : 조회한 Entity 의 값을 변경할 때 시간이 자동 저장됨
- 그리고 Posts 클래스가 BaseTimeEntity 를 상속받도록 변경
- main 메소드가 있는 클래스에 @EnableJpaAuditing 어노테이션 추가해서 활성화