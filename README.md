# 스프링 부트와 AWS로 혼자 구현하는 웹 서비스

# 챕터 별 내용 정리

[Chatper 2](https://github.com/5nam/practice/blob/master/%EC%8A%A4%ED%94%84%EB%A7%81%20%EB%B6%80%ED%8A%B8%EC%99%80%20AWS%EB%A1%9C%20%ED%98%BC%EC%9E%90%20%EA%B5%AC%ED%98%84%ED%95%98%EB%8A%94%20%EC%9B%B9%20%EC%84%9C%EB%B9%84%EC%8A%A4/Chatper%202.md)

[Chapter 3](https://github.com/5nam/practice/blob/master/%EC%8A%A4%ED%94%84%EB%A7%81%20%EB%B6%80%ED%8A%B8%EC%99%80%20AWS%EB%A1%9C%20%ED%98%BC%EC%9E%90%20%EA%B5%AC%ED%98%84%ED%95%98%EB%8A%94%20%EC%9B%B9%20%EC%84%9C%EB%B9%84%EC%8A%A4/Chapter%203.md)

# 실습 중 만난 오류들

- 오류 1 **java.lang.IllegalStateException: Unable to detect database type**

    ```
    # Caused by: java.lang.IllegalStateException: Unable to detect database type 오류 해결 시도 7
    #spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
    #spring.jpa.properties.hibernate.dialect.storage_engine=innodb
    ## Caused by: java.lang.IllegalStateException: Unable to detect database type 오류 해결 시도 6
    #spring.datasource.hikari.jdbc-url=jdbc:h2:mem://localhost/~/testdb;MODE=MYSQL
    #spring.datasource.hikari.username=sa
    ```

  → h2 연결 부분 모두 주석 처리 하니까 해결!
  - 참고 : [https://velog.io/@chang626/Chapter-08-EC2-서버에-프로젝트를-배포해-보자](https://velog.io/@chang626/Chapter-08-EC2-%EC%84%9C%EB%B2%84%EC%97%90-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EB%A5%BC-%EB%B0%B0%ED%8F%AC%ED%95%B4-%EB%B3%B4%EC%9E%90)

- 오류 2 : Caused by: java.sql.SQLSyntaxErrorException: (conn=2321) Table 'umcDB.SPRING_SESSION' doesn't exist

  - 해결 : [https://velog.io/@ojs0073/Table-SPRINGSESSION-not-found-SQL-statement-에러](https://velog.io/@ojs0073/Table-SPRINGSESSION-not-found-SQL-statement-%EC%97%90%EB%9F%AC)

- 오류 3 : 2023-06-07 07:26:34.316 ERROR 2959 --- [nio-8080-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : (conn=26) Table 'umcDB.posts' doesn't exist
  2023-06-07 07:26:34.347 ERROR 2959 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.dao.InvalidDataAccessResourceUsageException: could not extract ResultSet; SQL [n/a]; nested exception is org.hibernate.exception.SQLGrammarException: could not extract ResultSet] with root cause

  → 자동으로 ddl 을 해주는 설정 & 테이블 이름 설정 규칙을 지정을 빠뜨려서 오류가 남

    ```
    # java.sql.SQLSyntaxErrorException: (conn=26) Table 'umcDB.posts' doesn't exist 오류 해결 시도 1
    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    # java.sql.SQLSyntaxErrorException: (conn=26) Table 'umcDB.posts' doesn't exist 오류 해결 시도 3
    spring.jpa.hibernate.ddl-auto = update
    ```

- 한글 깨지는 오류

  [https://www.inflearn.com/questions/545116/한글이-깨지는것-같아요](https://www.inflearn.com/questions/545116/%ED%95%9C%EA%B8%80%EC%9D%B4-%EA%B9%A8%EC%A7%80%EB%8A%94%EA%B2%83-%EA%B0%99%EC%95%84%EC%9A%94)

- 글 등록 후, 메인 오류 페이지

  # Whitelabel Error Page

  This application has no explicit mapping for /error, so you are seeing this as a fallback.

  Wed Jun 14 04:55:46 UTC 2023

  There was an unexpected error (type=Internal Server Error, status=500).

    - 데이터베이스 확인해보니, `createdDate` , `modifiedDate` 가 null 로 되어있음
    - @EnableJpaAuditing 코드 main 에 추가하니 해결됨

# 데이터베이스

### Member

```sql
-- auto-generated definition
create table Member
(
    id           bigint auto_increment
        primary key,
    createdDate  datetime     null,
    modifiedDate datetime     null,
    email        varchar(255) not null,
    name         varchar(255) not null,
    picture      varchar(255) null,
    role         varchar(255) not null
);
```

| id(PK) | createdDate | modifiedDate | email | name | picture | role |
| --- | --- | --- | --- | --- | --- | --- |

### Posts

```sql
-- auto-generated definition
create table Posts
(
    id           bigint auto_increment
        primary key,
    createdDate  datetime     null,
    modifiedDate datetime     null,
    author       varchar(255) null,
    content      text         not null,
    title        varchar(500) not null
);
```

| id(PK) | createdDate | modifiedDate | author | content | title |
| --- | --- | --- | --- | --- | --- |
