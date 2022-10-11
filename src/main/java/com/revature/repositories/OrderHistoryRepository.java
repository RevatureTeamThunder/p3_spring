package com.revature.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.OrderHistory;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer>{

	List<OrderHistory> findByCustomerId(Integer customerId);

}
