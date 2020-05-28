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

























