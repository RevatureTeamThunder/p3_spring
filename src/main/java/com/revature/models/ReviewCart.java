package com.revature.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review_cart")
public class ReviewCart {
	
	@Id
	private Integer id;
	private Integer cartId;
	private Integer customerId;
	private Integer productId;
	private Integer quantity;
	private String name;
	private Integer price;
	private Integer totalCost;

}
