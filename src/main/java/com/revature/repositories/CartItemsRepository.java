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

	@Procedure(value = "purchase_items")
	public void purchase_items(int cartId);

	public void deleteCartItemsByProductId(int productId);

	Optional<CartItems> findByCustomerIdAndProductId(int customerId, int productId);

	void deleteAllByCustomerId(int customerId);
}
