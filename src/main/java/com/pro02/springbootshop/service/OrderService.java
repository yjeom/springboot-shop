package com.pro02.springbootshop.service;

import com.pro02.springbootshop.dto.OrderDto;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.Member;
import com.pro02.springbootshop.entity.Order;
import com.pro02.springbootshop.entity.OrderItem;
import com.pro02.springbootshop.repository.ItemRepository;
import com.pro02.springbootshop.repository.MemberRepository;
import com.pro02.springbootshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderDto orderDto,String email){
        Item item=itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityExistsException::new);
        Member member=memberRepository.findByEmail(email);

        List<OrderItem> orderItemList=new ArrayList<>();
        OrderItem orderItem=OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order=Order.createOrder(member,orderItemList);
        orderRepository.save(order);
        return order.getId();
    }
}
