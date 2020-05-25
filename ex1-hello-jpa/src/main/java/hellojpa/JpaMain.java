package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

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


            List<Member> result = em.createQuery("select m from Member as m where m.id > 1", Member.class)
                    .getResultList();

            for (Member mem: result) {
                System.out.println("member.name = " + mem.getName());
            }

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}
