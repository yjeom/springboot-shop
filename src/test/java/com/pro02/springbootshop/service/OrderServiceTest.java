package com.pro02.springbootshop.service;

import com.pro02.springbootshop.constant.ItemSellStatus;
import com.pro02.springbootshop.dto.OrderDto;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.Member;
import com.pro02.springbootshop.entity.Order;
import com.pro02.springbootshop.entity.OrderItem;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){
        Item item=new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member=new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName(value = "주문 테스트")
    public void orderTest(){
        Item item=saveItem();
        Member member=saveMember();

        OrderDto orderDto=new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        Long orderId= orderService.order(orderDto,member.getEmail());

        Order order=orderRepository.findById(orderId)
                .orElseThrow(EntityExistsException::new);
        List<OrderItem> orderItems=order.getOrderItems();
        int totalPrice=orderDto.getCount()*item.getPrice();
        assertEquals(totalPrice,order.getTotalPrice());

    }
}