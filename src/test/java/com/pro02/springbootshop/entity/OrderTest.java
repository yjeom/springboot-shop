package com.pro02.springbootshop.entity;

import com.pro02.springbootshop.constant.ItemSellStatus;
import com.pro02.springbootshop.repository.ItemRepository;
import com.pro02.springbootshop.repository.MemberRepository;
import com.pro02.springbootshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item=new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName(value = "영속성 전이 테스트")
    public void cascadeTest(){
        Order order=new Order();

        for(int i=1;i<=3;i++){
            Item item=this.createItem();
            itemRepository.save(item);

            OrderItem orderItem=new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        orderRepository.saveAndFlush(order);
        em.clear();

        Order savedOrder=orderRepository.findById(order.getId())
                .orElseThrow(EntityExistsException::new);
        assertEquals(3,savedOrder.getOrderItems().size());
    }

    public Order createOrder(){
        Order order=new Order();

        for(int i=1;i<=3;i++){
            Item item=createItem();
            itemRepository.save(item);

            OrderItem orderItem=new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            order.getOrderItems().add(orderItem);
        }
        Member member=new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName(value = "고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order=createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

}