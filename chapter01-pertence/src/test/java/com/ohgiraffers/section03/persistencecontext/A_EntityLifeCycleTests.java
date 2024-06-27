package com.ohgiraffers.section03.persistencecontext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class A_EntityLifeCycleTests {

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

    @AfterAll
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    @AfterEach
    public void closeManager() {
        entityManager.close();
    }

    /*
    * 영속성 컨텍스트는 엔티티 매니저가 엔티티 객체를 저장하는 공간, 엔티티 객체를 보관하고 관리함
    * 엔티티 매니저가 생성될 때 하나의 영속성 컨텍스트가 생성됨
    *
    * 엔티티 생명주기
    * 비영속, 영속, 준영속, 삭제 상태
    *
    * */

    @Test
    public void 비영속_테스트() {
         Menu foundMenu = entityManager.find(Menu.class, 1);
         Menu newMenu = new Menu();

         newMenu.setMenuCode(foundMenu.getMenuCode());
         newMenu.setMenuName(foundMenu.getMenuName());
         newMenu.setMenuPrice(foundMenu.getMenuPrice());
         newMenu.setCategoryCode(foundMenu.getCategoryCode());
         newMenu.setOrderableStatus(foundMenu.getOrderableStatus());

         boolean isTrue = (foundMenu == newMenu);

         Assertions.assertTrue(isTrue);
    }

    @Test
    public void 영속성_연속_조회_테스트() {
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 11);

        boolean isTrue = (foundMenu1 == foundMenu2);

        Assertions.assertTrue(isTrue);

    }

    @Test
    public void 영속성_객체_추가_테스트() {

        /* Menu Entity의 @GeneratedValue(strategy=GenerationType.IDENTITY) 설정을 잠시 주석하고 테스트 수행 */

        //given
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        //when
        entityManager.persist(menuToRegist);
        Menu foundMenu = entityManager.find(Menu.class, 500);
        boolean isTrue = (menuToRegist == foundMenu);

        //then
        Assertions.assertTrue(isTrue);
    }


    @Test
    public void 영속성_객체_추가_값_변경_테스트() {

        /* Menu Entity의 @GeneratedValue(strategy=GenerationType.IDENTITY) 설정을 잠시 주석하고 테스트 수행 */

        //given
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        //when
        entityManager.persist(menuToRegist);
        menuToRegist.setMenuName("메론죽");

        Menu foundMenu = entityManager.find(Menu.class, 500);

        //then
        Assertions.assertEquals("메론죽", foundMenu.getMenuName());
    }

    @Test
    public void 준영속성_detach_테스트() {

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        /* 영속성 컨텍스트가 관리하던 엔티티 객체를 관리하지 않는 상태가 된다면 준영속 상태라고 한다.
         * 그 중 detach는 특정 엔티티만 준영속 상태로 만든다. */
        //when
        entityManager.detach(foundMenu2);

        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        Assertions.assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());
        Assertions.assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());
    }

    @Test
    public void 준영속성_clear_테스트() {

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        //when
        /* clear()는 영속성 컨텍스트를 초기화 한다. */
        entityManager.clear();

        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        //then
        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());
        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());
    }


    @Test
    public void close_테스트() {

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        //when
        /* close()는 영속성 컨텍스트를 종료한다. */
        entityManager.close();

        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        //then
        /* 영속성 컨텍스트를 닫았기 때문에 다시 만들기 전에는 사용할 수 없다.
         * java.lang.IllegalStateException : Session/EntityManager is closed */
        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());
        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    public void 삭제_remove_테스트() {

        /* remove : 엔티티를 영속성 컨텍스트 및 데이터 베이스에서 삭제한다.
         * 단, 트랜잭션을 제어하지 않으면 영구 반영되지는 않는다.
         * 트랜잭션을 커밋하는 순간 영속성 컨텍스트에서 관리하는 엔티티 객체가 데이터 베이스에 반영되게 된다. (이를 flush라 한다.)
         * flush : 영속성 컨텍스트의 변경 내용을 데이터 베이스에 동기화 하는 작업(등록, 수정, 삭제한 엔티티를 데이터베이스에 반영) */

        //given
        Menu foundMenu = entityManager.find(Menu.class, 2);

        //when
        entityManager.remove(foundMenu);
        Menu refoundMenu = entityManager.find(Menu.class, 2);

        //then
        Assertions.assertEquals(2, foundMenu.getMenuCode());
        Assertions.assertEquals(null, refoundMenu);

    }

    /* 병합(merge) : 파라미터로 넘어온 준영속 엔티티 객체의 식별자 값으로 1차 캐시에서 엔티티 객체를 조회한다.
     * 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고 1차 캐시에 저장한다.
     * 조회한 영속 엔티티 객체에 준영속 상태의 엔티티 객체의 값을 병합한 뒤 영속 엔티티 객체를 반환한다.
     * 혹은 조회할 수 없는 데이터의 경우 새로 생성해서 병합한다. (save or update) */

    @Test
    public void 병합_merge_수정_테스트() {

        //given
        Menu menuToDetach = entityManager.find(Menu.class, 2);
        entityManager.detach(menuToDetach);

        //when
        menuToDetach.setMenuName("수박죽");
        Menu refoundMenu = entityManager.find(Menu.class, 2);

        /* 준영속 엔티티와 영속 엔티티의 해쉬코드는 다른 상태이다. */
        System.out.println(menuToDetach.hashCode());
        System.out.println(refoundMenu.hashCode());

        entityManager.merge(menuToDetach);

        //then
        Menu mergedMenu = entityManager.find(Menu.class, 2);
        Assertions.assertEquals("수박죽", mergedMenu.getMenuName());

    }

    @Test
    public void 병합_merge_삽입_테스트() {

        //given
        Menu menuToDetach = entityManager.find(Menu.class, 2);
        entityManager.detach(menuToDetach);

        //when
        menuToDetach.setMenuCode(999);         // DB에서 조회할 수 없는 키 값으로 변경
        menuToDetach.setMenuName("수박죽");

        entityManager.merge(menuToDetach);      // 영속 상태의 엔티티와 병합 (현재는 없기 때문에 삽입 된다)

        //then
        Menu mergedMenu = entityManager.find(Menu.class, 999);
        Assertions.assertEquals("수박죽", mergedMenu.getMenuName());

    }
}
