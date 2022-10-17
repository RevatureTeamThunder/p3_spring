package com.revature.repositories;

import com.revature.models.PurchasedItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface PurchasedItemsRepository extends JpaRepository<PurchasedItems, Long>
{
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByCartId(long cartId);
}