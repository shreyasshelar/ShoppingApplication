package com.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.ShoppingOrder;

public interface ShoppingOrderRepository extends JpaRepository<ShoppingOrder, Long> {

}
