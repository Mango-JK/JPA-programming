# JPA 활용

##  Spring Initializr 사용하기



#### Project

> Gradle Project

#### Language

> Java

#### Spring Boot

> 2.1.17 (SNAPSHOT)

#### Dependencies

>**Spring Web**
>
>**Thymeleaf**
>
>**Spring Data JPA**
>
>**H2 Database**
>
>**Lombok**



#### 핵심 라이브러리

- 스프링 MVC
- 스프링 ORM
- JPA, 하이버네이트
- 스프링 데이터 JPA



기타 라이브러리

- H2 데이터베이스 클라이언트
- 커넥션 풀: 부트 기본은 HikariCP
- WEB(thymeleaf)
- 로깅 SLF4J & LogBack
- 테스트



<hr/>
# 1. Entity Model

<center><image src="./img/entity_table.PNG"></image></center>
</center>

**회원(Member):** 이름과 임베디드 타입인 주소( Address ), 그리고 주문( orders ) 리스트를 가진다. 

**주문(Order):** 한 번 주문시 여러 상품을 주문할 수 있으므로 주문과 주문상품( OrderItem )은 일대다 관계 다. 주문은 상품을 주문한 회원과 배송 정보, 주문 날짜, 주문 상태( status )를 가지고 있다. 주문 상태는 열 거형을 사용했는데 주문( ORDER ), 취소( CANCEL )을 표현할 수 있다. 

**주문상품(OrderItem):** 주문한 상품 정보와 주문 금액( orderPrice ), 주문 수량( count ) 정보를 가지고 있다. (보통 OrderLine , LineItem 으로 많이 표현한다.) 

**상품(Item):** 이름, 가격, 재고수량( stockQuantity )을 가지고 있다. 상품을 주문하면 재고수량이 줄어든 다. 상품의 종류로는 도서, 음반, 영화가 있는데 각각은 사용하는 속성이 조금씩 다르다. 

**배송(Delivery):** 주문시 하나의 배송 정보를 생성한다. 주문과 배송은 일대일 관계다. 

**카테고리(Category):** 상품과 다대다 관계를 맺는다. parent , child 로 부모, 자식 카테고리를 연결한 다. 

**주소(Address):** 값 타입(임베디드 타입)이다. 회원과 배송(Delivery)에서 사용한다. 

<br/>

> 참고: 회원이 주문을 하기 때문에, 회원이 주문리스트를 가지는 것은 얼핏 보면 잘 설계한 것 같지만, 객체 세 상은 실제 세계와는 다르다. 실무에서는 회원이 주문을 참조하지 않고, 주문이 회원을 참조하는 것으로 충분 하다. 여기서는 일대다, 다대일의 양방향 연관관계를 설명하기 위해서 추가했다.

<br/>

# 2. Entity



- 예제에서는 설명을 쉽게하기 위해 엔티티 클래스에 Getter, Setter를 모두 열고, 최대한 단순하게 설계 
- 실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용하는 것을 추천



>  참고: 이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적 이다. 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이 편리하다. Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만 Setter는 문제가 다르다. Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 가까운 미래에 엔티티에가 도대 체 왜 변경되는지 추적하기 점점 힘들어진다. 그래서 엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확 하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 한다.

<br/>

#### 회원엔티티

```java
package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

>  참고: 엔티티의 식별자는 id 를 사용하고 PK 컬럼명은 member_id 를 사용했다. 엔티티는 타입(여기서는 Member )이 있으므로 id 필드만으로 쉽게 구분할 수 있다. 테이블은 타입이 없으므로 구분이 어렵다. 그리 고 테이블은 관례상 테이블명 + id 를 많이 사용한다. 참고로 객체에서 id 대신에 memberId 를 사용해도 된다. 중요한 것은 일관성이다.

<br/>

#### 주문 엔티티

```java
package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Table(name = "orders")
@Entity
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]
}
```

<br/>

<hr/>
##### <실무에서는 @ManyToMany 를 사용하지 말자 > 

**@ManyToMany** 는 편리한 것 같지만, 중간 테이블( CATEGORY_ITEM )에 컬럼을 추가할 수 없고, 세밀하게 쿼 리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다. 

**중간 엔티티( CategoryItem 를 만들고 @ManyToOne , @OneToMany 로 매핑해서 사용하자.** 



정리하면 **다대다** 매핑을 **일대다, 다대일 매핑**으로 풀어 내서 사용하자.

<hr/>
## Entity 설계시 주의점

### 엔티티에는 가급적 Setter를 사용하지 말자

Setter가 모두 열려있다. 변경 포인트가 너무 많아서, 유지보수가 어렵다.



### 모든 연관관계는 지연로딩으로 설정!

- 즉시로딩('EAGER')은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N+1문제가 자주 발생한다.
- 실무에서 모든 연관관계는 지연로딩('LAZY')으로 설정해야 한다.
- 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 이용한다.
- @XToOne(**OneToOne, ManyToOne**)관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 한다.



### 컬렉션은 필드에서 초기화 하자.

컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.

- null 문제에서 안전하다.
- 하이버네이트는 엔티티를 영속화 할 때, 컬랙션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한 다. 만약 getOrders() 처럼 임의의 메서드에서 컬력션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문 제가 발생할 수 있다. 따라서 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다.



<hr/>

#### CascadeType.ALL

#####  --> persist(itemsA)

##### --> persist(itemsB)

##### --> persist(item)

컬렉션 필드가 있는 경우 이렇게 각각 persist해줘야 하는데



**CascadeType.ALL** 속성을 사용한다면 persist(item) 하나로 가능하다.

<hr/>

<center><image src="./img/fetch_join.PNG"></image></center>



### 실무에서 성능을 위해 fetch join을 사용하자.

​	**N + 1 문제**에서 벗어날 수 있다.

<br/>

<center><image src="./img/selectV4.PNG"></image></center>

<br/>

#### JPA에서 DTO로 바로 조회.

DTO를 이용해 쿼리에서 직접 DTO를 만들어서 가져오게 되면,

필요한 컬럼들만 찾아오므로 성능이 좋다.

<hr/>



### 정리

**엔티티를 DTO로 변환**하거나, DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다.

둘중 상황에 따라서 더 나은 방법을 선택하면 된다. 

**엔티티로 조회하면**

 **리포지토리 재사용성도 좋고**,

 **개발도 단순**해진다. 

따라서 권장하는 방법은 다음과 같다.

<br/>

#### 쿼리 방식 선택 권장 순서

1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하면 페치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈 해결
3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.

<hr/>























