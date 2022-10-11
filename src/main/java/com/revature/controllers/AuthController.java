package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.dtos.LoginRequest;
import com.revature.dtos.RegisterRequest;
import com.revature.exceptions.CustomerNotFoundException;
import com.revature.models.Customer;
import com.revature.repositories.CustomerRepository;
import com.revature.services.AuthService;
import org.springframework.http.HttpStatus;
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

    /*
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam(name = "email", required = true) String email,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "firstName", required = true) String firstName,
            @RequestParam(name = "lastName", required = true) String lastName,
            @RequestParam(name = "role", required = false) Optional<String> role
    )
    {
        // TODO change to throw exception
        if(customerRepository.existsByEmail(email))
        {
            return ResponseEntity.status(400).body("Email already taken.");
        }
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setRole(role.orElse("User"));
        return ResponseEntity.status(201).body(customerRepository.save(customer));
    }

     */

    @Authorized
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable("id") int id
    ) throws CustomerNotFoundException
    {
        if(!customerRepository.existsById(id))
        {
            throw new CustomerNotFoundException();
        }
        customerRepository.deleteById(id);
        return ResponseEntity.status(204).body("");
    }

    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        Customer created = new Customer(0,
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                "User");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(created));
    }
}
