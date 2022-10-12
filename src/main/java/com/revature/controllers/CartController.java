package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.CartNotFoundException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.Cart;
import com.revature.models.CartItems;
import com.revature.models.ReviewCart;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ReviewCartRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController
{
    private CartRepository cartRepository;
    private CartItemsRepository cartItemsRepository;
    private ReviewCartRepository reviewCartRepository;
    private ProductRepository productRepository;

    public CartController(CartRepository cartRepository, CartItemsRepository cartItemsRepository, ReviewCartRepository reviewCartRepository, ProductRepository productRepository)
    {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.reviewCartRepository = reviewCartRepository;
        this.productRepository = productRepository;
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
    ) throws JSONException, CartNotFoundException
    {
        JSONObject shoppingCart = new JSONObject();
        Optional<List<ReviewCart>> reviewCartList = reviewCartRepository.findAllByCustomerId(customerId);
        if(reviewCartList.isPresent())
        {
            BigDecimal cart_total = BigDecimal.valueOf(0);
            int x = 0;
            for(ReviewCart rc : reviewCartList.get())
            {
                JSONObject obj = new JSONObject();
                obj.put("id", rc.getId());
                obj.put("product_id", rc.getProductId());
                obj.put("customer_id", rc.getCustomerId());
                obj.put("name", rc.getName());
                obj.put("quantity", rc.getQuantity());
                obj.put("price", rc.getPrice());
                obj.put("total_cost", rc.getTotalCost());
                cart_total = rc.getTotalCost().add(cart_total);
                shoppingCart.put(String.valueOf(x), obj);
                ++x;
            }
            shoppingCart.put("cart_total", cart_total);
            return ResponseEntity.ok(shoppingCart);
        }
        throw new CartNotFoundException();
    }

    @Authorized
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteItemFromCart(
            @PathVariable("id") int cartId,
            @RequestParam(name = "product_id", required = true) int productId
    ) throws ProductNotFoundException, CartNotFoundException
    {
        Optional<List<CartItems>> cartItemsList = cartItemsRepository.findAllByCartId(cartId);
        if(cartItemsList.isPresent())
        {
            if(!productRepository.existsById(productId))
            {
                throw new ProductNotFoundException();
            }
            cartItemsRepository.deleteCartItemsByProductId(productId);
            return ResponseEntity.status(204).body("");
        }
        throw new CartNotFoundException();
    }
}
