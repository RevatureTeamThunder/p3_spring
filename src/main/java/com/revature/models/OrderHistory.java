package com.revature.models;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_history")
public class OrderHistory {

	 	@Id
	    private int id;
	 	private Integer customerId;
	 	private Integer productId;
	 	private Integer cartId;
	 	private Integer quantity;
	 	private String name;
	 	private String price;
	 	private BigDecimal totalCost;
}
