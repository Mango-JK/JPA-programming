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

# #7 영속성 관리

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





