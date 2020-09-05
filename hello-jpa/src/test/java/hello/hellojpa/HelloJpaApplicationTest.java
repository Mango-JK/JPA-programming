package hello.hellojpa;

import hello.hellojpa.domain.Member;
import hello.hellojpa.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HelloJpaApplicationTest {

	MemoryMemberRepository repository = new MemoryMemberRepository();

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

		Member member3 = new Member();
		member3.setName("spring3");
		repository.save(member3);

		List<Member> result = repository.findAll();

		assertThat(result.size()).isEqualTo(2);
	}

}
