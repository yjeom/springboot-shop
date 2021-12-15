package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Cart findByMemberId(Long memberId);
}
