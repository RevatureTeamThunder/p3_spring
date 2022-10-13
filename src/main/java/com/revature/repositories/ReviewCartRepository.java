package com.revature.repositories;

import com.revature.models.ReviewCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewCartRepository extends JpaRepository<ReviewCart, Long>
{
    @Query(value = "SELECT * from review_cart where customer_id = :customerId", nativeQuery = true)
    public Optional<List<ReviewCart>> findAllByCustomerId(int customerId);
}
