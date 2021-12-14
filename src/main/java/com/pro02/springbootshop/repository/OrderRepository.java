package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o "+
            "WHERE o.member.email = :email "+
            "ORDER BY o.orderDate DESC")
    List<Order> findOrders(@Param("email")String email, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o "+
            "WHERE o.member.email = :email")
    Long ocuntOrder(@Param("email")String email);
}
