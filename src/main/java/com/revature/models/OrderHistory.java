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
@Table(name = "order_history")
public class OrderHistory {

	 	@Id
	    private long id;
	 	private Integer customerId;
	 	private Integer productId;
	 	private Integer cartId;
	 	private Integer quantity;
	 	private String productname;
	 	private String price;
	 	private Integer totalCost;
}
