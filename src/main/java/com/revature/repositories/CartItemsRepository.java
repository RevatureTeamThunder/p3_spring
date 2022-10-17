package com.revature.repositories;

import com.revature.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Integer>{

	List<CartItems> findByCustomerId(Integer customerId);

	Optional<List<CartItems>>  findAllByCartId(int cartId);

	Optional<List<CartItems>> findAllByCartId(long cartId);


	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "CALL purchase_items(:cartId)", nativeQuery = true)
	void purchase_items(int cartId);

	@Transactional
	@Modifying
	void deleteCartItemsByProductIdAndCartId(int productId, long cartId);

	Optional<CartItems> findByCustomerIdAndProductId(long customerId, int productId);

	@Transactional()
	@Modifying
	void deleteAllByCustomerId(int customerId);

	Optional<CartItems> findByCartIdAndProductId(long cartId, int productId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from cart_items where cart_id = :cartId", nativeQuery = true)
	void deleteAllByCartId(long cartId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "DELETE FROM purchased_items WHERE cart_id = :cartId", nativeQuery = true)
	void deletePurchasedItemsByCartId(long cartId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteById(long id);
}
