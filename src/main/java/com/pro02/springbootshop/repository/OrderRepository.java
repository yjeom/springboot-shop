package com.pro02.springbootshop.repository;

import com.pro02.springbootshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
