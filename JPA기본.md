# 🎓 자바 ORM 표준 JPA 프로그래밍

<br/>

## #3 SQL  중심적인 개발의 문제점

- 무한 반복, 지루한 코드

- 필드 수정시, 관련된 모든 SQL 수정

- SQL 의존적인 개발을 피하기 어렵다.

- 패러다임의 불일치 "객체 vs RDBS"

  <br/>

- **객체와 관계형 데이터베이스의 차이**

  - 상속
    - 객체는 상속관계가 있지만 DB에는 없다.
  - 연관관계
    - 객체는 참조를 사용, 테이블은 외래키를 사용
    - 객체는 연관된 클래스를 가져올 수 있지만, DB는 조인을 해야 함. (번거로움) 😓
  - 데이터 타입
  - 데이터 식별 방법

<br/>

<hr/>
## #4 JPA 소개

- **JPA?**
  - Java Persistence API
  - 자바 진영의 **ORM** 기술 표준
- **ORM?**
  - Object-relational mapping (객체 관계 매핑)
  - 객체는 객체대로 설계
  - RDBMS는 RDBMS대로 설계
  - ORM 프레임워크가 중간에서 매핑
- JPA는 애플리케이션과 JDBC 사이에서 동작

<center><image src="./img/JPA동작.PNG"></image></center>
<br/>

### JPA를 왜 사용해야 하는가?

- SQL 중심적인 개발에서 객체 중심으로 개발

- 생산성

  - persist, find, member.setName, remove 등 간편하게 사용

- 유지보수

  - 기존 : 필드 변경시 모든 SQL을 수정해야 함

- 패러다임 불일치 해결

- 성능

  - 1차 캐시와 동일성 보장
  - 트랜잭션을 지원하는 쓰기 지연 (INSERT)
  - 지연 로딩과 즉시 로딩

  <br/>
  
  <hr/>

# #5 Hello JPA - 프로젝트 생성

### H2 데이터베이스 설치와 실행

- http://www.h2database.com/
- 최고의 실습용 DB
- 가볍다
- MySQL, Oracle 데이터베이스 시뮬레이션 기능

<br/>

<center><image src="./img/H2Console.PNG"></center>
<center><image src="./img/H2Init.PNG"></center>
<br/>

### 메이븐 소개

- https://maven.apache.org/

- 자바 라이브러리, 빌드 관리

- 라이브러리 자동 다운로드 및 의존성 관리

- 최근에는 그래들(Gradle)이 점점 유명

  <hr/>
### 라이브러리 추가 - pom.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>jpa-basic</groupId>
    <artifactId>ex1-hello-jpa</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <!-- JPA 하이버네이트 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.3.10.Final</version>
        </dependency>

        <!-- H2 데이터베이스 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.199</version>
        </dependency>

    </dependencies>

</project>
```

<hr/>
### JPA 설정하기 - persistence.xml

- JPA 설정 파일
- /META_INF/persistence.xml 위치
- javax.persistence로 시작 : JPA 표준 속성
- hibernate로 시작 : 하이버네이트 전용 속성

<br/>

```java
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
    <persistence-unit name="hello">
        <!-- 데이터베이스당 하나의 영속성 name은 영속성 고유의 이름으로 지정 -->
        <properties>
            <!-- 필수 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <!-- MySQLDialect, OracleDialect, H2Dialect 등 환경에 맞게 사용 -->

            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
            <property name="hibernate.id.new_generator_mappings" value="true"/>
            <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
        </properties>
    </persistence-unit>
</persistence>
```

<hr/>
# #6 Hello JPA 애플리케이션 개발

### JPA 구동 방식

<center><image src="./img/JPA동작방식.PNG"></center>
<hr/>
### Member Entity 만들기

```java
package hellojpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
//@Table(name = "USER")
public class Member {

    @Id
    private Long id;
    //@Column("username")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

<hr/>
기본적인 CRUD 실습

```java
package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember = em.find(Member.class, 1L);

//            기본적인 추가작업 CREATE
//            Member member = new Member();
//            member.setId(3L);
//            member.setName("HelloC");
//            em.persist(member);

//            기본적인 삭제작업 REMOVE
//            em.remove(findMember);

//            기본적인 수정 작업 UPDATE
//            findMember.setName("HelloJPA");

//            기본적인 조회
            System.out.println("findmember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}
```

### ※ 주의

- **엔티티 매니저 팩토리**는 하나만 생성해서 애플리케이션 전체에서 공유
- **엔티티 매니저**는 쓰레드간에 공유X (사용하고 버려야 함)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

<br/>

### JPQL 소개

- 가장 단순한 조회 방법
  
  - EntityManager.find()
- 나이가 18살 이상인 회원을 모두 검색하고 싶다면...?

- JPQL로 전체 데이터 조회

  ```sql
  List findMembers = em.createQuery("select m from Member", Member.class).getResultList();
  ```

- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능

<br/>

### JPQL vs SQL

- **JPQL은 엔티티 객체를 대상**으로 쿼리 

- **SQL은 데이터베이스 테이블을 대상**으로 쿼리

<br/>

# #7 영속성 관리 ⭐

### JPA에서 가장 중요한 2가지

- 객체와 관계형 데이터베이스 매핑하기(Object Relational Mapping)
- **영속성 컨텍스트** : "엔티티를 영구 저장하는 환경"

<br/>

### 엔티티의 생명주기

- 비영속

```java
//객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

<br/>

- 영속

  - 영속성 컨텍스트에 **관리**되는 상태

  ```java
  //객체를 생성한 상태(비영속)
  Member member = new Member();
  member.setId("member1");
  member.setUsername("회원1");
  
  Entitymanager em = emf.createEntityManager();
  em.getTransaction().begin();
  
  // 객체를 저장한 상태(영속)
  em.persist(member);
  ```

  

- 준영속

  - 영속성 컨텍스트에 저장되었다가 **분리**된 상태

    ```java
    em.detach(member);
    ```

    

- 삭제

  - **삭제**된 상태

```java
em.remove(member);
```

<br/>

```java
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //비영속
            Member member = new Member();
            member.setId(100L);
            member.setName("HelloJPA");
            //영속
            System.out.println("==== BEFORE ====");
            em.persist(member);
            //준영속
            em.detach(member);
            //삭제
            em.remove(member);
            System.out.println("==== AFTER ====");

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
```

<br/>

### 영속성 컨텍스트의 이점

- 1차 캐시

<center><image src="./img/1차캐시.PNG"></center>
- 동일성(identity) 보장

```java
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");

System.out.println(a == b);	// 동일성 비교 true
```

​	1차 캐시로 반복 가능한 읽기 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공

<br/>

- 트랜잭션을 지원하는 쓰기 지연

```java
EntityManager em = emf.createEntitymanager();
EntityTransaction tx = em.getTransaction();
//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
tx.begin();

em.persist(memberA);
em.persist(memberB);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.

//커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
tx.commit();	// [트랜잭션] 커밋
```

<br/>

- 변경 감지

```java
EntityManager em = emf.createEntitymanager();
EntityTransaction tx = em.getTransaction();
tx.begin();	// 트랜잭션 시작

//영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

//영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

tx.commit();	// 트랜잭션 커밋
```

<br/>

- 지연 로딩

<br/>

### 플러시

- 영속성 컨텍스트의 변경내용을 데이터베이스에 반영

### 플러시 발생하면 무슨 일이 생기나?

- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제 쿼리)

<br/>

**영속성 컨텍스트를 플러시하는 방법**

- em.flush() - 직접 호출
- 트랜잭션 커밋 - 플러시 자동 호출
- JPQL 쿼리 실행 - 플러시 자동 호출

**플러시는 영속성 컨텍스트를 비우지 않음**

**트랜잭션이라는 작업 단위가 중요하다! -> 커밋 직전에만 동기화 하면 됨**

<hr/>
# #12 엔티티 매핑

### 객체와 테이블 매핑 : @Entity, @Table

**@Entity**

- @Entity가 붙은 클래스는 JPA가 관리, 엔티티라 한다.

- JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수

  **기본 생성자** 필수 

  final클래스, enum, interface, inner 클래스 사용 X

**@Table**

- @Table은 엔티티와 매핑할 테이블 지정
  - name : 매핑할 테이블 이름
  - catalog, schema : 데이터베이스 catalog, schema 매핑

### 필드와 컬럼 매핑 : @Column

### 기본키 매핑 : @Id

### 연관관계 매핑 : @ManyToOne, @JoinColumn

<hr/>
# #13 데이터베이스 스키마 자동 생성

```java
// 데이터베이스 스키마 자동설정
// persistence.xml에서 속성을 지정
<property name="hibernate.hbm2ddl.auto" value="create" />
```



| 옵션        | 설명                                         |
| ----------- | -------------------------------------------- |
| create      | 기존테이블 삭제 후 다시 생성 (DROP + CREATE) |
| create-drop | create와 같으나 종료시점에 테이블 DROP       |
| update      | 변경분만 반영(운영DB에는 사용하면 안됨)      |
| validate    | 엔티티와 테이블이 정상 매핑되었는지만 확인   |
| none        | 사용하지 않음                                |

<br/>

### ⚠ 운영 장비에는 절대 create, create-drop, update 사용하면 안된다

- 개발 초기 단계는 create 또는 update
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none

<br/>

# #14 필드와 컬럼 매핑

### 요구사항 추가

1. 회원은 일반 회원과 관리자로 구분해야 한다.

2. 회원 가입일과 수정일이 있어야 한다.

3. 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.

   <hr/>
```java
   // 회원을 구분하기 위한 enum 생성
   package hellojpa;
   
   public enum RoleType {
       USER, ADMIN
   }
```



<hr/>
```java
package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Member {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

// 데이터의 컨텐츠가 아주 많은 경우
    @Lob
    private String description;

    public Member(){
    }
}
```

<hr/>
### 매핑 어노테이션 정리

| 어노테이션  | 설명                               |
| ----------- | ---------------------------------- |
| @Column     | 컬럼 매핑                          |
| @Temporal   | 날짜 타입 매핑                     |
| @Enumerated | enum 타입 매핑                     |
| @Lob        | BLOB, CLOB 매핑                    |
| @Transient  | 특정 필드를 컬럼에 매핑시키지 않음 |

<br/>

### @Column

| 속성                       | 설명                                                         | 기본값                              |
| -------------------------- | ------------------------------------------------------------ | ----------------------------------- |
| name                       | 필드와 매핑할 테이블의 컬럼 이름                             | 객체의 필드 이름                    |
| insertable,<br />updatable | 등록, 변경 가능 여부                                         | TRUE                                |
| nullable(DDL)              | null 값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다. |                                     |
| unique(DDL)                | @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다. |                                     |
| columnDefinition           | 데이터베이스 컬럼 정보를 직접 줄 수 있다.<br />ex) varchar(100) default 'EMPTY' | 필드의 자바 타입과 방언 정보를 사용 |
| length(DDL)                | 문자 길이 제약조건, String 타입에만 사용                     | 255                                 |
| precision,<br />scale(DDL) | BigDecimal 타입에서 사용한다(BigInteger도 사용할 수 있다)<br />precision은 소수점을 포함한 전체 자릿수를, scale은 소수의 자릿수다. 참고로 double, float 타입에는 적용되지 않는다. | precision=19                        |

<hr/>
### @Enumerated

자바 Enum 타입을 매핑할 때 사용

### ⚠ ORDINAL 사용 금지 !

<hr/>
### @Temporal

날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용

**참고 : LocalDate, LocalDateTime을 사용할 때는 생략 가능(하이버네이트 지원)**

<hr/>
### @Lob

- Lob에는 지정할 수 있는 속성이 없다.
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑

<hr/>
# #15 기본키 매핑

### 기본키 매핑 어노테이션

- **@ID**

- ### @GeneratedValue

```java
@Id @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

<br/>

### 기본키 매핑 방법

- 직접 할당 : @Id만 사용
- 자동 생성(@GeneratedValue)
  - **IDENTITY** : 데이터베이스에 위임, MYSQL
  - **SEQUENCE** : 데이터베이스 시퀀스 오브젝트 사용
  - **TABLE** : 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - **AUTO** : 방언에 따라 자동 지정

<hr/>
### IDENTITY 전략 - 특징

- 기본키 생성을 데이터베이스에 위임
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
- JPA는 보통 트랝개션 커밋 시점에 INSERT SQL 실행
- AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID값을 알 수 있음.
- IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL을 실행하고 DB에서 식별자를 조회

```java
// IDENTITY 전략의 경우 PK값을 설정하지 않는다.
// 하지만 이럴 때, PK값을 DB에 넣기 전까지 알 수가 없다

// ---> em.persist(member) 로 일단 호출을 한 시점에
// member.getId(); 를 사용하여 조회한다.
        try {
            Member member = new Member();
            member.setUsername("CA");
            System.out.println("==============");
            em.persist(member);
            System.out.println("member.id =  " + member.getId());
            System.out.println("==============");

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
```

<br/>

### SEQUENCE 전략  - 특징

이와 대조적으로 SEQUENCE 전략에서는,

INSERT QUERY를 날리기 전에 SEQUENCE 값을 먼저 받아온 뒤 실행한다.

--> 영속성 컨텍스트에 KEY값들이 쌓여 있어서 버퍼링(쭉 모았다가 INSERT)이 가능

미리 **allocationSize를 50**으로 잡아두고 호출 -> **성능 최적화**에 사용됨

<br/>

# #16 실전 예제 1 - 요구사항 분석과 기본 매핑

### 요구사항 분석

- 회원은 상품을 주문할 수 있다.
- 주문 시 여러 종류의 상품을 선택할 수 있다.

<br/>

### 기능 목록

- 회원기능
  - 회원등록
  - 회원조회
- 상품 기능
  - 상품등록
  - 상품수정
  - 상품조회
- 주문기능
  - 상품주문
  - 주문내역조회
  - 주문취소

<hr/>
### 도메인 모델 분석

- **회원과 주문의 관계** : **회원**은 여러 번 **주문**할 수 있다. (일대다)
- **주문**과 **상품**의 관계 : **주문**할 때 여러 **상품**을 선택할 수 있다. 반대로 같은 상품도 여러 번 주문될 수 있다. **주문상품** 이라는 모델을 만들어서 다대다 관계를 일대다, 다대일 관계로 풀어냄

<center><image src="./img/도메인모델분석.PNG"></center>
<center><image src="./img/테이블설계.PNG"></center>
<hr/>
@ **springboot**에서 **DB table**에 컬럼을 만들 때 **카멜케이스**를 기본적으로

**소문자 + 언더스코어**로 변환한다.

ex) orderDate --> order_date

<hr/>
### 테이블 중심 설계의 문제점

- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능

<hr/>
# #17 연관관계 매핑 기초

### 용어 이해

- **방향**(Direction) : 단방향, 양방향
- **다중성** : 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M) 이해
- **연관관계의 주인(Owner)** : 객체 양방향 연관관계는 관리 주인이 필요

<br/>

### 예제 시나리오

- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.

<br/>

<hr/>
### 객체를 테이블에 맞추어 모델링

(식별자로 다시 조회, 객체 지향적인 방법은 아니다.)

```java
// 조회
Member member = em.find(Member.class, member.getId());

//연관관계가 없음
Team findTeam = em.find(Team.class, team.getId());
```

이 방법은 연관관계가 없기 때문에 계속 영속성을 부여하고 DB에 조회해야 함.

**좋지 않은 방법**

<br/>

### 단방향 연관관계

<center><image src="./img/단방향_모델링.PNG"></center>

<br/>

<br/>

### 객체 지향 모델링

(연관관계 저장)

```java
// 팀 저장
Team team = new Team();
team.setName("TeamA");
em.persist(team);

//회원 저장
Member member = new Member();
member.setName("member1");
member.setTeam(team);	// 단방향 연관관계 설정, 참조 저장
em.persist(member)

```

<br/>

# #18 양방향 연관관계와 주인 ⭐

<center><image src="./img/양방향_모델링.PNG"></center>

<br/>

### 연관관계의 주인과 mappedBy

- mappedBy = JPA의 멘탈붕괴 난이도
- mappedBy는 처음에는 이해하기 어렵다.
- 객체와 테이블간에 연관관계를 맺는 차이를 이해해야 한다.

<br/>

### 객체와 테이블이 관계를 맺는 차이

- 객체 연관관계 = 2개
  - 회원 --> 팀 연관관계 1개(단방향)
  - 팀 --> 회원 연관관계 1개(단방향)
- 테이블 연관관계 = 1개
  - 회원 <--> 팀의 연관관계 1개(양방향)

<br/>

<hr/>

### 객체의 양방향 관계

- 객체의 양방향 관계는 **사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개**다.
- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.

<br/>

### 테이블의 양방향  연관관계

- 테이블은 외래 키 하나로 두 테이블의 연관관계를 관리
- MEMBER.TEAM_ID 외래 키 하나로 양방향 연관관계 가짐

```sql
SELECT *
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

SELECT *
FROM TEAM T
JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

<hr/>

## 딜레마가 온다 - MEMBER, TEAM 중 무슨 KEY를 봐야하나?

<center><image src="./img/외래키.PNG"></center>

<br/>

# 연관관계의 주인(Owner)

### 양방향 매핑 규칙

- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
- **연관관계의 주인만이 외래 키를 관리(등록, 수정)**
- **주인이 아닌쪽은 읽기만 가능**
- 주인은 mappedBy 속성 사용X
- 주인이 아니면 mappedBy 속성으로 주인 지정

<br/>

<center><image src="./img/주인.PNG"></center>

<br/>

<hr/>

## ⚠ 양방향 매핑시 가장 많이 하는 실수

(연관관계의 주인에 값을 입력하지 않음)

<center><image src="./img/양방향_주의.PNG"></center>

<hr/>

###  양방향 매핑시 연관관계의 주인에 값을 입력해야 한다.

(순수한 객체 관계를 고려하면 항상 양쪽다 값을 입력해야 한다.)

```java
           Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            
            team.getMembers().add(member);

            //연관관계의 주인에 값 설정
            member.setTeam(team);	// **
            
            em.persist(member);
```

<hr/>

## ⚠  양방향 연관관계 주의

- ### 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자

- **연관관계 편의 메소드**를 생성하자

```java
public void changeTeam(Team team){
	this.team = team;
	
	team.getMembers().add(this);
}
```

<br/>

- 양방향 매핑시에 무한 루프를 조심하자

  - 예: toString(), lombok, JSON 생성 라이브러리
  - **Controller에서 Entity JSON 반환은 하지 말자!**

  <hr/>

  ## 양방향 매핑 정리

- **단방향 매핑만으로도 이미 연관관계 매핑은 완료**

- 양방향 매핑은 반대 방향으로 조회 기능이 추가된 것 뿐

- JPQL에서 역방향으로 탐색할 일이 많음

- 단방향 매핑을 잘 하고 양방향은 필요할 때 추가해도 됨

  (테이블에 영향을 주지 않음)

<br/>

# #20 실전 예제 2 - 연관관계 매핑 시작

<center><image src="./img/테이블구조_2.PNG"></center>

- **하나의 멤버는 여러 개의 주문을 할 수가 있고,**
- **주문과 아이템 사이의 다대다 관계를 1:N 과 N:1 로 풀어냈다.**

<br/>

<center><image src="./img/객체구조_2.PNG"></center>











