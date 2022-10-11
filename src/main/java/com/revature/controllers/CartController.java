package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.CartNotFoundException;
import com.revature.models.Cart;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController
{
    private CartRepository cartRepository;
    private CartItemsRepository cartItemsRepository;

    public CartController(CartRepository cartRepository, CartItemsRepository cartItemsRepository)
    {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
    }

    @Authorized
    @GetMapping("/{id}")
    public ResponseEntity<?> getCartById(
            @PathVariable("id") int cartId,
            @RequestParam(value = "customer_id", required = true) int customerId
    ) throws NoPermissionException, CartNotFoundException
    {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if(cart.isPresent())
        {
            if(cart.get().getCustomerId() != customerId)
            {
                throw new NoPermissionException();
            }
            return ResponseEntity.ok(cart.get());
        }
        throw new CartNotFoundException();
    }

    @Authorized
    @GetMapping("/view")
    public ResponseEntity<?> viewAllItemsInCart(
            @RequestParam(name = "id", required = true) int customerId
    )
    {
        // TODO implement a JPA Repository method to get these.
        return ResponseEntity.ok("");
    }
}
