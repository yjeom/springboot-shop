package com.pro02.springbootshop.service;

import com.pro02.springbootshop.dto.CartDetailDto;
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
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList=new ArrayList<>();

        Member member=memberRepository.findByEmail(email);
        Cart cart=cartRepository.findByMemberId(member.getId());
        if(cart ==null){
            return cartDetailDtoList;
        }
        cartDetailDtoList=cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validate(Long cartItemId,String email){
        Member curMember=memberRepository.findByEmail(email);
        CartItem cartItem=cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        Member savedMember=cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public  void updateCartItem(Long cartItemId,int count){
        CartItem cartItem=cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId){
        CartItem cartItem=cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);

        cartItemRepository.delete(cartItem);
    }
}
