package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.Cart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer>{

    @Transactional
    @Modifying
    public void deleteByCustomerIdAndPurchased(int customerId, boolean b);

    Optional<Cart> findByCartId(long cartId);

    @Transactional
    @Modifying
    public void deleteByCartIdAndPurchased(long cartId, boolean b);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from cart where cart_id = :cartId", nativeQuery = true)
    public void deleteByCartId(long cartId);

    boolean existsByCartId(long cartId);
}
