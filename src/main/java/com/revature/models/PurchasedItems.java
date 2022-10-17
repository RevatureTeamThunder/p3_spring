package com.revature.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchased_items")
public class PurchasedItems {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private Integer customerId;
	private Integer productId;
	private Integer cardId;
	private Integer quantity;
	private Long cartId;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Integer getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(Integer customerId)
	{
		this.customerId = customerId;
	}

	public Integer getProductId()
	{
		return productId;
	}

	public void setProductId(Integer productId)
	{
		this.productId = productId;
	}

	public Integer getCardId()
	{
		return cardId;
	}

	public void setCardId(Integer cardId)
	{
		this.cardId = cardId;
	}

	public Integer getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Integer quantity)
	{
		this.quantity = quantity;
	}

	public Long getCartId()
	{
		return cartId;
	}

	public void setCartId(Long cartId)
	{
		this.cartId = cartId;
	}
}
