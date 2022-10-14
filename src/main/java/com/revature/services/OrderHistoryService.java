package com.revature.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.exceptions.OrderHistoryNotFoundException;
import com.revature.models.OrderHistory;
import com.revature.repositories.OrderHistoryRepository;

@Service
public class OrderHistoryService {

	OrderHistoryRepository orderHistoryRepository;
	
	public OrderHistoryService(OrderHistoryRepository orderHistoryRepository) {
		this.orderHistoryRepository = orderHistoryRepository;
	}
	
	//View the order id, date, and total cost. 
	public Optional<List<OrderHistory>> findByCartId(Integer id) throws OrderHistoryNotFoundException {
		Optional<List<OrderHistory>> orderHistory = orderHistoryRepository.findByCartId(id);
		if(orderHistory.isPresent()) {
		return orderHistory;
		} else {
			throw new OrderHistoryNotFoundException("Order History Not Found");
		}
	}
	
	//View all order History
	public List<OrderHistory> findAll(){
		return orderHistoryRepository.findAll();
	}
	
	//List all products, quantity, and cost of the order by customer id
	public Optional<List<OrderHistory>> viewAllOrderHistoryOfCustomer(Integer customerId){
		return orderHistoryRepository.findByCustomerIdOrderByCartId(customerId);
	}

	/*
	//Puts all items from the shopping cart into the order history.
	public void saveToOrderHistory(Integer cartId, OrderHistory orderHistory) throws OrderHistoryNotFoundException {
		OrderHistory orderHistoryNew = this.findById(cartId);
		orderHistoryRepository.save(orderHistoryNew);
	}

	 */
}
