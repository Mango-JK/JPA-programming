# 🎓 스프링 부트와 JPA 활용 1

<br/>

<center><image src="./img/Settings.PNG"></center>
<br/>

**build.gradle**을 실행하여 dependency를 모두 받아준 이후

1. **plugins -> lombok -> Install**
2. **preferenct -> Annotaion Processors 설정**

<br/>

<hr/>
 핵심 라이브러리

- 스프링 MVC
- 스프링 ORM
- JPA, 하이버네이트
- 스프링 데이터 JPA

<hr/>
### application.yml 설정

```java
sping:
  datasource:
    url: jdbc:h2:tcp://localhost/~/jpashop;MVCC=TRUE
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        show-sql: true
        format_sql: true
logging:
  level:
    org.hibernate.SQL: debug
    
```

<hr/>
### Member Entity

```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;
}
```

### Member Repository

```java
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
```

<br/>

### MemberRepository Test

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest extends TestCase {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
```

<br/>

<hr/>
## 도메인 분석 설계

<center><image src="./img/archi_1.PNG"></center>
<center><image src="./img/archi_2.PNG"></center>
<center><image src="./img/archi_3.PNG"></center>

<br/>

<center><image src="./img/tips_2.PNG"></center>

<br/>

<center><image src="./img/tips_3.PNG"></center>

<br/>

<hr/>

<br/>

<center><image src="./img/tips_1.PNG"></center>

<br/>

<br/>

## 엔티티 클래스 개발

<center><image src="./img/tips_4.PNG"></center>

<br/>

<hr/>

## 엔티티 설계시 주의점



### 엔티티에는 가급적 Setter를 사용하지 말자

### Setter가 모두 열려 있다. 변경 포인트가 너무 많아서, 유지보수가 어렵다.



### 모든 연관관계는 지연로딩으로 설정!

- 즉시로딩('EAGER')은 예측이 어렵고, 어떤 SQL이 실행될지 추측하기 어렵다. 특히 JPQL을 실행할 때 N+1 문제가 자주 발생한다.
- 실무에서 모든 연관관계는 지연로딩('LAZY')으로 설정해야 한다.
- @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 한다.

<br/>

### 컬렉션은 필드에서 초기화 하자.

컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.

- null 문제에서 안전하다.

<br/>

## 애플리케이션 아키텍처

<center><image src="./img/architecture.PNG"></center>

<br/>

### 패키지 구조

- jpabook.jpashop

  - domain
  - exception
  - repository
  - service
  - web

  <br/>

  ### 개발 순서

: 서비스, 리포지토리 계층을 개발하고, 테스트 케이스를 작성해서 검증, 마지막에 웹 계층 적용

<hr/>

## 회원 도메인 개발

<center>Member</center>

```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
```

<br/>

<hr/>

<center>MemberRepository</center>

```java
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
```

<br/>

<hr/>

<center>MemberService</center>

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);    // 중복 회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
```

