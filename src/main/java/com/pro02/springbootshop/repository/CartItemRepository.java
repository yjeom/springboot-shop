package com.pro02.springbootshop.repository;


import com.pro02.springbootshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByCartIdAndItemId(Long cartId,Long itemId);
}
