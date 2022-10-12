package com.revature.repositories;

import com.revature.models.ReviewCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewCartRepository extends JpaRepository<ReviewCart, Long>
{
    public Optional<List<ReviewCart>> findAllByCustomerId(int customerId);
}
