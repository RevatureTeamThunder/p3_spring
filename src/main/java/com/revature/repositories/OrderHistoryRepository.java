package com.revature.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.OrderHistory;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer>{

	Optional<List<OrderHistory>> findByCustomerId(Integer customerId);

    Optional<List<OrderHistory>> findByCartId(Integer id);

    Optional<List<OrderHistory>> findAllByCartId(int id);
}
