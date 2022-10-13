package com.revature.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.CartItems;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;

public interface CartItemsRepository extends JpaRepository<CartItems, Integer>{

	List<CartItems> findByCustomerId(Integer customerId);

	Optional<List<CartItems>>  findAllByCartId(int cartId);

	Optional<List<CartItems>>  findAllByCartId(long cartId);


	@Procedure(value = "purchase_items")
	public void purchase_items(int cartId);

	@Transactional
	@Modifying
	public void deleteCartItemsByProductIdAndCartId(int productId, long cartId);

	Optional<CartItems> findByCustomerIdAndProductId(long customerId, int productId);

	@Transactional()
	@Modifying
	public void deleteAllByCustomerId(int customerId);

	public Optional<CartItems> findByCartIdAndProductId(long cartId, int productId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "delete from cart_items where cart_id = :cartId", nativeQuery = true)
	public void deleteAllByCartId(long cartId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "DELETE FROM purchased_items WHERE cart_id = :cartId", nativeQuery = true)
	public void deletePurchasedItemsByCartId(long cartId);
}
