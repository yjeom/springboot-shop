package com.pro02.springbootshop.service;

import com.pro02.springbootshop.dto.CartItemDto;
import com.pro02.springbootshop.entity.Cart;
import com.pro02.springbootshop.entity.CartItem;
import com.pro02.springbootshop.entity.Item;
import com.pro02.springbootshop.entity.Member;
import com.pro02.springbootshop.repository.CartItemRepository;
import com.pro02.springbootshop.repository.CartRepository;
import com.pro02.springbootshop.repository.ItemRepository;
import com.pro02.springbootshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto,String email){
        Item item=itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityExistsException::new);
        Member member=memberRepository.findByEmail(email);

        Cart cart=cartRepository.findByMemberId(member.getId());
        if(cart==null){
            cart=Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem=cartItemRepository
                .findByCartIdAndItemId(cart.getId(),item.getId());

        if(savedCartItem!=null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{
            CartItem cartItem=CartItem.createCartItem(cart,item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
}
