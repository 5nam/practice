package carrot.practice.domain.posts;

import carrot.practice.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * DB 테이블과 매칭될 클래스 : Entity 클래스라고도 함
 * JPA 를 사용하면서 DB 데이터에 작업할 경우 실제 쿼리를 날리기보다, 이 Entity 클래스의 수정을 통해 작업 가능
 * Entity 테이블에는 Setter 메소드를 절대 만들지 X
 */

@Getter
@NoArgsConstructor // 기본 생성자 자동 추가
@Entity // 테이블과 링크될 클래스임을 나타냄. 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름을 매칭(SalesManager.java -> sales_manager table)
public class Posts extends BaseTimeEntity {

    @Id // 해당 테이블의 PK 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 의 생성 규칙을 나타냄
    private Long id;

    @Column(length = 500, nullable = false) // 테이블의 카럼을 나타내며 굳이 선언하지 않아도 해당 클래스의 필드는 모두 칼럼이 됨
    // 사용하는 이유는 기본 값 ㅗ이에 추가로 변경이 필요한 옵션이 있으면 사용
    // 문자열의 경우 VARCHAR(255) 가 기본값인데, 사이즈를 500으로 늘리고 싶거나, 타입을 TEXT 로 변경하고 싶은 등의 경우에 사용
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder // 해당 클래스의 빌더 패턴 클래스를 생성. 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author  = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
