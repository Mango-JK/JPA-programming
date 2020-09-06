# JPA 입문



### IntelliJ Gradle 대신에 자바 직접 실행

- 최근 IntelliJ 버전은 Gradle을 통해서 실행 하는 것이 기본 설정이다. 이렇게 하면 실행속도가 느리다. 다음과 같이 변경하면 자바로 바로 실행해서 실행속도가 더 빠르다.
- Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle
  - Build and run using: Gradle -> IntelliJ IDEA
  - Run tests using: Gradle -> IntelliJ IDEA

> **윈도우 사용자** File -> Setting



### 라이브러리 살펴보기

#### 스프링 부트 라이브러리

- spring-boot-starter-web
  - spring-boot-starter-**tomcat: 톰캣** (웹서버)
  - spring-**webmvc: 스프링 웹 MVC**
- spring-boot-starter-**thymeleaf: 타임리프** 템플릿 엔진
- spring-boot-starter(공통): **스프링 부트 + 스프링 코어 + 로깅**
  - spring-boot
    - spring-core
  - spring-boot-starter-logging
    - logback, slf4j

> 요즘에는 logback을 많이 사용함.
>
> 성능이 좋다.

<br/>

#### 테스트 라이브러리

- spring-boot-starter-test
  - junit: 테스트 프레임워크
  - mockito: 목 라이브러리
  - assertj: 테스트 코드를 좀 더 편하게 작성하게 도와주는 라이브러리
  - spring-test: 스프링 통합 테스트 지원

<br/>

#### View 환경설정

**Welcome Page 만들기**

```html
<!DOCTYPE HTML>
<html>
<head>
	<title>Hello</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
Hello
<a href="/hello">hello</a>
</body>
</html>
```

<br/>

#### Thymeleaf 템플릿 엔진

공식 사이트 : https://www.thymeleaf.org/

스프링 공식 튜토리얼 : https://spring.io/guides/gs/serving-web-content/

<br/>

```java
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!");
        return "hello";
    }
}
```

<br/>

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<p th:text="'안녕하세요. ' + ${data}">안녕하세요. 손님</p>
</body>
</html>
```

**thyleleaf 템플릿엔진 동작 확인**

- 실행 : http://localhost:8080/hello

<hr/>

<center><image src="./img/동작환경그림.PNG"></image></center>

<br/>

<hr/>

>  참고 : **'spring-boot-devtools'** 라이브러리 추가하면, html 파일을 컴파일만 해주면 서버 재시작 없이 View 파일 변경이 가능하다.
>
> IntelliJ 컴파일 방법 : 메뉴 build -> Recompile

<br/>

#### 빌드하고 실행하기

콘솔로 이동

1. **./gradlew build**
2. **cd build/libs**
3. **java -jar hello-spring-0.0.1-SNAPSHOT.jar**
4. 실행 확인

<br/>

```java
// 윈도우 사용자를 위한 팁
콘솔로 이동 -> 명령 프롬프트(cmd)로 이동
./gradlew -> gradlew.bat를 실행하면 됩니다.
    gradlew.bat build
폴더 목록 확인 ls -> dir
    
* 윈도우에서 Git bash 터미널 사용하기
https://medium.com/@violetboralee/intellij-idea%EC%99%80-git-bash-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0-63e8216aa7de
```

<center><image src="./img/intelliJ_gitbash.PNG"></image></center>

<hr/>

#### 

### 스프링 웹 개발 기초

- 정적 컨텐츠
  - 파일을 그대로 고객에게 전달
- MVC와 템플릿 엔진
  - 서버에서 변형을 해서 내려주는 방식
- API
  - 안드로이드, 아이폰 클라이언트와 개발 할 때 JSON 포맷으로 내려주는 방식

<br/>

#### #1 정적 컨텐츠

<center><image src="./img/정적컨텐츠.PNG"></image></center>

<br/>

<hr/>

#### #2 MVC와 템플릿 엔진

```java
    @GetMapping("hello-mvc")
    public String helloMVC(@RequestParam(value = "name", required = false) String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }
```

<br/>

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<p th:text="'안녕하세요. ' + ${name}">Hello empty !</p>
</body>
</html>
```

<br/>

**실행**

- http://localhost:8080/hello-mvc?name=mango

<br/>

<center><image src="./img/MVC과정.PNG"></image></center>



1. 웹 브라우저에서 localhost:8080/hello-mvc 값을 넘기면
2. 내장 톰켓 서버에서 Controller에서 알맞는 메서드를 매핑시킨다.
3. viewResolver가 알맞은 화면을 찾아 반환

<br/>

<hr/>

#### #3 API

**@ResponseBody** - 결과 값에 데이터를 그대로 내려준다.

- 1. **@ResponseBody 문자 반환**

  ```java
  @Controller
  public class HelloController{
  
  	@GetMapping("hello-string")
  	@ResponseBody
  	public String helloString(@ResponseParam("name") String name) {
  		return "hello" + name;
  	}
  }
  ```

@ResponseBody를 사용하면 뷰 리졸버를 사용하지 않음.

대신에 HTTP의 BODY에 문자 내용을 직접 반환



- 2. **@ResponseBody 객체 반환**

```java
    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
```

@ResponseBody를 사용하고, 객체를 반환하면 객체가 **JSON**으로 반환됨.

<br/>

<center><image src="./img/api사용.PNG"></image></center>

<hr/>

### 회원 관리 예제 - 백엔드 개발

#### #1 비즈니스 요구사항 정리

- 데이터 : 회원ID, 이름
- 기능 : 회원 등록, 조회
- 아직 데이터 저장소가 선정되지 않음(가상의 시나리오)

<br/>

**컨트롤러** : 웹 MVC의 컨트롤러 역할

**서비스** : 핵심 비즈니스 로직 구현

**리포지토리** : 데이터베이스에 접근, 도메인 객체를 DB에 저장하고 관리

**도메인** : 비즈니스 도메인 객체, 예) 회원, 주문, 쿠폰 등등 주로 데이터베이스에 저장하고 관리됨

<br/>

<hr/>

#### #2 회원 도메인과 리포지토리 만들기

**회원 객체**

```java
public class Member {

    private Long id;
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

<br/>

**리포지토리 인터페이스**

```java
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
```

<br/>

 **회원 리포지토리 메모리 구현체**

```java
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
    
    public void clearStore() {
        store.clear();
    }
}
```

<br/>

#### #3 회원 리포지토리 테스트 케이스 작성

```java
@SpringBootTest
class HelloJpaApplicationTest {

	MemoryMemberRepository repository = new MemoryMemberRepository();

    // 각각의 Test 작업을 마치고 Store를 clear하여 상호간 에러 방지
	@AfterEach
	public void AfterEach(){
		repository.clearStore();
	}

	@Test
	public void save(){
		Member member = new Member();
		member.setName("spring");

		repository.save(member);

		Member result = repository.findById(member.getId()).get();
		assertThat(member).isEqualTo(result);
	}

	@Test
	public void findByName(){
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);

		Member member2 = new Member();
		member2.setName("spring");
		repository.save(member2);

		Member result = repository.findByName("spring1").get();

		assertThat(result).isEqualTo(member1);
	}

	@Test
	public void findAll() {
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);

		Member member2 = new Member();
		member2.setName("spring2");
		repository.save(member2);

		List<Member> result = repository.findAll();

		assertThat(result.size()).isEqualTo(2);
	}
}
```

<br/>

**@AfterEach** : 한번에 여러 테스트를 실행하면 메모리 DB에 직전 테스트의 결과가 남을 수 있다. 

이렇게 되면 다음 이전 테스트 때문에 다음 테스트가 실패할 가능성이 있다.

 **@AfterEach**를 사용하면 **각 테스트가 종료될 때 마다 이 기능을 실행**한다. 

여기서는 메모리 DB에 저장된 데이터를 삭제한다. 

<br/>

**테스트는 각각 독립적으로 실행되어야 한다. 테스트 순서에 의존관계가 있는 것은 좋은 테스트가 아니다.**

<br/>

#### ## Optional 바르게 사용하기

```java
// 안 좋음
Optional<Member> member = ...;
if (member.isPresent()) {
    return member.get();
} else {
    return null;
}

// 좋음
Optional<Member> member = ...;
return member.orElse(null);



// 안 좋음
Optional<Member> member = ...;
if (member.isPresent()) {
    return member.get();
} else {
    throw new NoSuchElementException();
}

// 좋음
Optional<Member> member = ...;
return member.orElseThrow(() -> new NoSuchElementException());
```

<br/>

**`orElse(...)`에서 `...`는 `Optional`에 값이 있든 없든 무조건 실행된다. 따라서 `...`가 새로운 객체를 생성하거나 새로운 연산을 수행하는 경우에는 `orElse()` 대신 `orElseGet()`을 써야한다.**

<br/>

**단지 값을 얻는 경우라면 Optional 대신 null 비교를 사용**

```java
// 안 좋음
return Optional.ofNullable(status).orElse(READY);

// 좋음
return status != null ? status : READY;
```

<br/>

**Optional 대신 비어 있는 컬렉션 반환**

```java
//Spring Data JPA Repository 메서드 선언 시 
//다음과 같이 컬렉션을 Optional로 감싸서 반환하는 것은 좋지 않다. 
//컬렉션을 반환하는 Spring Data JPA Repository 메서드는
//null을 반환하지 않고 비어있는 컬렉션을 반환해주므로, 
//Optional로 감싸서 반환할 필요가 없다.

// 안 좋음
public interface MemberRepository<Member, Long> extends JpaRepository {
    Optional<List<Member>> findAllByNameContaining(String part);
}

// 좋음
public interface MemberRepository<Member, Long> extends JpaRepository {
    // null이 반환되지 않으므로 Optional 불필요
    List<Member> findAllByNameContaining(String part);      
}
```

<br/>

#### 회원 서비스 개발

```java
@Service
public class MemberSerivce {

    private final MemberRepository memberRepository = new MemoryMemberRepository();

    /**
     *
     * 회원 가입
     */
    public Long join(Member member) {
        // 같은 이름이 있는 중복 회원 X
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findByName(member.getName());
        result.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```

<br/>

#### 회원 서비스 테스트

**기존에는 회원 서비스가 메모리 회원 리포지토리를 직접 생성하게 했다.**

```java
public class MemberService {
	private final MemberRepository memberRepository = new MemoryMemberRepository();
}
```

<br/>

**회원 서비스 코드를 DI 가능하게 변경한다.**

```java
public class MemberSerivce {

	private final MemberRepository memberRepository;
	
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	...
}
```

<br/>

**회원 서비스 테스트**

```java
class MemberSerivceTest {

    MemberSerivce memberSerivce;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberSerivce = new MemberSerivce(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("hello");

        //when
        Long saveId = memberSerivce.join(member);

        //then
        Member findMember = memberSerivce.findOne(saveId).get();
        Assertions.assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberSerivce.join(member1);
        assertThrows(IllegalStateException.class, () -> memberSerivce.join(member2));

    }
}
```

<br/>

**@BeforeEach** : 각 테스트 실행 전에 호출된다. 테스트가 서로 영향이 없도록 항상 새로운 객체를 생성하고, 의존관계도 새로 맺어준다. 

<hr/>

<br/>

### 스프링 빈과 의존관계

#### 컴포넌트 스캔과 자동 의존관계 설정

회원 컨트롤러가 회원 서비스와 회원 리포지토리를 사용할 수 있게 의존관계를 준비하자.

<br/>

##### **회원 컨트롤러에 의존관계 추가** (1. 컴포넌트 스캔 방식)

```java
@Controller
public class MemberController {

    private final MemberSerivce memberSerivce;

    @Autowired
    public MemberController(MemberSerivce memberSerivce){
        this.memberSerivce = memberSerivce;
    }
}
```

- 생성자에 **@Autowired**가 있으면 스프링이 연관된 객체를 스프링 컨테이너에서 찾아서 넣어준다. 이렇게 객체 의존관계를 외부에서 넣어주는 것을  **DI (Dependency Injection), 의존성 주입**이라 한다.
- 이전 테스트에서는 개발자가 직접 주입했고, 여기서는 @Autowired에 의해 스프링이 주입해준다.

<br/>

> 참고: 스프링은 스프링 컨테이너에 스프링 빈을 등록할 때, 기본으로 **싱글톤**으로 등록한다(유일하게 하나만 등록해서 공유한다) 따라서 같은 스프링 빈이면 모두 같은 인스턴스다. 설정으로 싱글톤이 아니게 설정할 수 있지만, 특별한 경우를 제외하면 대부분 싱글톤을 사용한다.

<br/>

##### 자바 코드로 직접 스프링 빈 등록하기 (2. 스프링 빈 직접 등록)

```java
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }
    
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
```

> 참고: XML로 설정하는 방식도 있지만 최근에는 잘 사용하지 않으므로 생략한다.

<br/>

**실무에서는 주로 정형화된 컨트롤러, 서비스, 리포지토리 같은 코드는 컴포넌트 스캔을 사용한다.**

<hr/>

