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
@Table(name = "product_review_view")
public class ProductReviewView {

	@Id
	private long reviewId;
	private Integer customerId;
	private Integer productId;
	private Integer rating;
	private String productComments;
	private String firstName;
	private String lastName;
	private String name;
}
