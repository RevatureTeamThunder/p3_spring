package com.revature.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer
{
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    
	public Customer(int id, String email, String password, String firstName, String lastName) {
		super();
		this.customerId = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}
    
    
}
