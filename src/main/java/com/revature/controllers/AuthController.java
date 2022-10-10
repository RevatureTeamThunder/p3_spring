package com.revature.controllers;

import com.revature.dtos.LoginRequest;
import com.revature.models.Customer;
import com.revature.repositories.CustomerRepository;
import com.revature.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://p3-client.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    private final CustomerRepository customerRepository;
    
    

    public AuthController(AuthService authService, CustomerRepository customerRepository) {
        this.authService = authService;

        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<Customer> optional = authService.findByCredentials(loginRequest.getEmail(), loginRequest.getPassword());

        if(!optional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        session.setAttribute("user", optional.get());

        return ResponseEntity.ok(optional.get());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.removeAttribute("user");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam(name = "email", required = true) String email,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "firstName", required = true) String firstName,
            @RequestParam(name = "lastName", required = true) String lastName,
            @RequestParam(name = "role", required = false) Optional<String> role
    )
    {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setRole(role.orElse("User"));
        return ResponseEntity.status(201).body(customerRepository.save(customer));
    }

    /*
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        User created = new User(0,
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName());
              
        		

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(created));
    } */
}
