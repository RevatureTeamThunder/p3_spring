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
	public OrderHistory findById(Integer id) throws OrderHistoryNotFoundException {
		Optional<OrderHistory> orderHistory = orderHistoryRepository.findById(id);
		if(orderHistory.isPresent()) {
		return orderHistory.get();
		} else {
			throw new OrderHistoryNotFoundException("Order History Not Found");
		}
	}
	
	//View all order History
	public List<OrderHistory> findAll(){
		return orderHistoryRepository.findAll();
	}
	
	//List all products, quantity, and cost of the order by customer id
	public List<OrderHistory> viewAllOrderHistoryOfCustomer(Integer customerId){
		return orderHistoryRepository.findByCustomerId(customerId);
	}
	
	//Puts all items from the shopping cart into the order history.
	public void saveToOrderHistory(Integer cartId, OrderHistory orderHistory) throws OrderHistoryNotFoundException {
		OrderHistory orderHistoryNew = this.findById(cartId);
		orderHistoryRepository.save(orderHistoryNew);
	}
}
