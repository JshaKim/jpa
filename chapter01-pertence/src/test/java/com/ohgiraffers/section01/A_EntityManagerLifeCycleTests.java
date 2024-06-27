package com.ohgiraffers.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class A_EntityManagerLifeCycleTests {
    /*
    * 엔티티 매니저 팩토리란?
    * 엔티티 매닞러ㅡㄹ 생성할 수 있는 기능을 제공하는 팩토리 클래스
    * thread-safe하기 때문에 여러 스레드가 동시에 접근해도 안전하므로 서로 다른 스레드 간 공유 / 재사용 가능
    * thread-safeg한 기능을 요청 스코프마다 생성하기에는 비용 부담이 크므로
    * application스코프와 동일하게 싱글톤으로 생성해서 관리하는 것이 효율적
    * 따라서 데이터 베이스를 사용하는 애플리케이션 당 한 개의 EntityManagerFactory를 생성한다
    *
    * */

    /*
    * 엔티티 매니저란?
    * 엔티티 매니저는 엔티티를 저장하는 메모리 상의 DB를 관리하는 인스턴스
    * 엔티티를 저장, 수정, 삭제, 조회하는 등의 엔티티와 관련된 모든 일 수행
    * 엔티티 매니저는 thread-self하지 않기 때문에 동시성 문제 발생할 수 있음
    * 따라서 스레드간 공유를 하지 않고 web의 경우 일반적으로 request scop와 일치 시김
    *
    * */

    /*
    * 영속성 컨텍스트란(PersistenceContext)?
    * 엔티티 매니저를 통해 엔티티를 저장하고나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관하고 관리함
    * 영속성 컨텍스트는 엔티티를 key-value 방식으로 저장하는 저장소
    * 영속성 컨텍스트는 엔티티 매니절르 생성할 때 하나 만들어짐
    * 그리고 엔티티 매니저를 통해 영속성 컨텍스트에 접근 / 관리 가능
    * */

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    @BeforeAll
    public static void initFactory() {
        entityManagerFactory= Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인() {
        System.out.println("entityManagerFactory.hashcode: " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashcode: " + entityManager.hashCode());
    }


}
