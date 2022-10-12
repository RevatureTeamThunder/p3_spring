package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer>{

    void deleteByCustomerIdAndPurchased(int customerId, boolean b);

    Optional<Cart> findByCartId(long cartId);
}
