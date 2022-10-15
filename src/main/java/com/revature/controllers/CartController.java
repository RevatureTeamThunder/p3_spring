package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.CartItemNotFoundException;
import com.revature.exceptions.CartNotFoundException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.Cart;
import com.revature.models.CartItems;
import com.revature.models.ReviewCart;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ReviewCartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://p3-client.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class CartController
{
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ReviewCartRepository reviewCartRepository;
    private final ProductRepository productRepository;

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
            @PathVariable("id") long cartId,
            @RequestParam(value = "customer_id") int customerId
    ) throws NoPermissionException, CartNotFoundException
    {
        Optional<Cart> cart = cartRepository.findByCartId(cartId);
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
    @GetMapping("/customer_id/{id}")
    public ResponseEntity<?> getCartBCustomerId(
            @PathVariable("id") Integer customerId
    ) throws NoPermissionException, CartNotFoundException
    {
        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
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
            @RequestParam(name = "customer_id") int customer_id
    ) throws CartNotFoundException
    {
        // JSONObject shoppingCart = new JSONObject();
        Optional<List<ReviewCart>> reviewCartList = reviewCartRepository.findAllByCustomerId(customer_id);
        if(reviewCartList.isPresent())
        {

            /*
            BigDecimal cart_total = BigDecimal.valueOf(0);
            int x = 0;
            JSONArray arr = new JSONArray();
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
                arr.put(obj);
                ++x;
            }
             */

            if(reviewCartList.get().size() == 0)
            {
                throw new CartNotFoundException();
            }
            return ResponseEntity.ok(reviewCartList.get());
        }
        throw new CartNotFoundException();
    }

    @Authorized
    @PutMapping("/create")
    public ResponseEntity<?> createCart(
            @RequestParam(name = "customer_id") int customerId
    )
    {
        Cart cart = new Cart();
        cart.setCustomerId(customerId);
        cart.setPurchased(false);
        return ResponseEntity.status(201).body(cartRepository.save(cart));
    }

    @Authorized
    @PutMapping("/{id}/add")
    public ResponseEntity<?> addItemToCart(
            @PathVariable("id") long cartId,
            @RequestParam(name = "product_id") int productId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity
    ) throws CartNotFoundException
    {

        Optional<Cart> cart = cartRepository.findByCartId(cartId);
        if(cart.isPresent())
        {
            CartItems cartItems = new CartItems();
            cartItems.setCartId(cartId);
            cartItems.setProductId(productId);
            cartItems.setQuantity(quantity);
            cartItems.setCustomerId(cart.get().getCustomerId());
            return ResponseEntity.status(201).body(cartItemsRepository.save(cartItems));
        }
        throw new CartNotFoundException();
    }

    @Authorized
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteItemFromCart(
            @PathVariable("id") long cartId,
            @RequestParam(name = "product_id") int productId
    ) throws ProductNotFoundException, CartNotFoundException
    {
        Optional<List<CartItems>> cartItemsList = cartItemsRepository.findAllByCartId(cartId);
        if(cartItemsList.isPresent())
        {
            if(!productRepository.existsByProductId(productId))
            {
                throw new ProductNotFoundException();
            }
            cartItemsRepository.deleteCartItemsByProductIdAndCartId(productId, cartId);
            return ResponseEntity.status(204).body("");
        }
        throw new CartNotFoundException();
    }

    @Authorized
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable("id") long cartId,
            @RequestParam(name = "product_id") int productId,
            @RequestParam(name = "quantity") int quantity
    ) throws CartItemNotFoundException
    {
        Optional<CartItems> cartItems = cartItemsRepository.findByCartIdAndProductId(cartId, productId);

//            int custId = cartItems.get().getCustomerId();
//            System.out.println(custId);
        if(cartItems.isPresent())
        {
            CartItems updateItem = new CartItems();
            updateItem.setId(cartItems.get().getId());
            updateItem.setQuantity(quantity);
            updateItem.setCartId(cartItems.get().getCartId());
            updateItem.setProductId(productId);
            updateItem.setCustomerId(cartItems.get().getCustomerId());
            return ResponseEntity.ok(cartItemsRepository.save(updateItem));
        }

        if (!cartItems.isPresent())
        {
//                System.out.println(custId);
            Optional<Cart> cart = cartRepository.findByCartId(cartId);
            int custId = 0;
            if (cart.isPresent()) {
                Cart existingCart = cart.get();
                custId = existingCart.getCustomerId();
            }

            CartItems cartNewItems = new CartItems();
            cartNewItems.setCartId(cartId);
            cartNewItems.setProductId(productId);
            cartNewItems.setQuantity(quantity);
            cartNewItems.setCustomerId(custId);
            return ResponseEntity.status(201).body(cartItemsRepository.save(cartNewItems));
        }
        throw new CartItemNotFoundException();
    }

    @Authorized
    @DeleteMapping("{id}/empty")
    public ResponseEntity<?> emptyCart(
            @PathVariable("id") long cartId
    ) throws CartNotFoundException
    {
        Optional<Cart> cart = cartRepository.findByCartId(cartId);
        if(cart.isPresent())
        {
            cartItemsRepository.deleteAllByCartId(cartId);
            cartItemsRepository.deletePurchasedItemsByCartId(cartId);
            cartRepository.deleteByCartId(cartId);
            return ResponseEntity.status(204).body("");
        }
        throw new CartNotFoundException();
    }
}
