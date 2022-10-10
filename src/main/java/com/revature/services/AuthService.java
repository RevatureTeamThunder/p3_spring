package com.revature.services;

import com.revature.models.Customer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public Optional<Customer> findByCredentials(String email, String password) {
        return userService.findByCredentials(email, password);
    }

    public Customer register(Customer customer) {
        return userService.save(customer);
    }
}
