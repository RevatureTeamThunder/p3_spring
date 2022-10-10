package com.revature.services;

import com.revature.models.Customer;
import com.revature.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final CustomerRepository customerRepository;

    public UserService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findByCredentials(String email, String password) {
        return customerRepository.findByEmailAndPassword(email, password);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
