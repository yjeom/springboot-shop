package com.pro02.springbootshop.service;

import com.pro02.springbootshop.dto.OrderDto;
import com.pro02.springbootshop.dto.OrderHistDto;
import com.pro02.springbootshop.dto.OrderItemDto;
import com.pro02.springbootshop.entity.*;
import com.pro02.springbootshop.repository.ItemImgRepository;
import com.pro02.springbootshop.repository.ItemRepository;
import com.pro02.springbootshop.repository.MemberRepository;
import com.pro02.springbootshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

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
    private final ItemImgRepository itemImgRepository;

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

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders=orderRepository.findOrders(email,pageable);
        Long totalCount=orderRepository.ocuntOrder(email);

        List<OrderHistDto> orderHistDtos=new ArrayList<>();

        for(Order order:orders){
            OrderHistDto orderHistDto=new OrderHistDto(order);
            List<OrderItem> orderItems=order.getOrderItems();
            for(OrderItem orderItem:orderItems){
                ItemImg itemImg=itemImgRepository
                        .findByItemIdAndRepImgYn(orderItem.getItem().getId(),"Y");
                OrderItemDto orderItemDto=new OrderItemDto(orderItem,itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId,String email){
        Member curMember=memberRepository.findByEmail(email);
        Order order=orderRepository.findById(orderId).orElseThrow(
                EntityExistsException::new
        );
        Member saveMember=order.getMember();
        if(!StringUtils.equals(curMember.getEmail(),saveMember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId){
        Order order=orderRepository.findById(orderId)
                .orElseThrow(EntityExistsException::new);
        order.cancelOrder();
    }

    public Long orders(List<OrderDto>orderDtoList,String email){
        Member member=memberRepository.findByEmail(email);
        List<OrderItem> orderItemList=new ArrayList<>();

        for(OrderDto orderDto:orderDtoList){
            Item item=itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityExistsException::new);
            OrderItem orderItem=OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }
        Order order=Order.createOrder(member,orderItemList);
        orderRepository.save(order);
        return order.getId();
    }
}
