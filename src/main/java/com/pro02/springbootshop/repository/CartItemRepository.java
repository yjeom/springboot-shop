package com.pro02.springbootshop.repository;


import com.pro02.springbootshop.dto.CartDetailDto;
import com.pro02.springbootshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByCartIdAndItemId(Long cartId,Long itemId);

    @Query("SELECT new com.pro02.springbootshop.dto.CartDetailDto(ci.id,i.itemName,i.price," +
            "ci.count,im.imgUrl) " +
            "FROM CartItem ci, ItemImg im " +
            "JOIN ci.item i " +
            "WHERE ci.cart.id=:cartId " +
            "AND im.item.id=ci.item.id " +
            "AND im.repImgYn = 'Y' " +
            "ORDER BY ci.regTime DESC")
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
