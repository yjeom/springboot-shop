package com.pro02.springbootshop.service;

import com.pro02.springbootshop.constant.ItemSellStatus;
import com.pro02.springbootshop.dto.CartItemDto;
import com.pro02.springbootshop.entity.CartItem;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.Member;
import com.pro02.springbootshop.repository.CartItemRepository;
import com.pro02.springbootshop.repository.ItemRepository;
import com.pro02.springbootshop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartService cartService;

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
    @DisplayName(value = "장바구니 담기 테스트")
    public void addCartTest(){
        Item item=saveItem();
        Member member=saveMember();

        CartItemDto cartItemDto=new CartItemDto();
        cartItemDto.setItemId(item.getId());
        cartItemDto.setCount(5);

        Long cartItemId= cartService.addCart(cartItemDto,member.getEmail());

        CartItem cartItem=cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);

        assertEquals(item.getId(),cartItem.getItem().getId());
        assertEquals(cartItemDto.getCount(),cartItem.getCount());
    }

}