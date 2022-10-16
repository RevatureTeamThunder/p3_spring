package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.CartItemNotFoundException;
import com.revature.exceptions.NotEnoughProductQuantityException;
import com.revature.exceptions.OrderHistoryNotFoundException;
import com.revature.models.CartItems;
import com.revature.models.OrderHistory;
import com.revature.models.Product;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import com.revature.repositories.OrderHistoryRepository;
import com.revature.repositories.ProductRepository;
import com.revature.services.OrderHistoryService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://p3-client.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class OrderController
{
    private final OrderHistoryService orderHistoryService;

    private final OrderHistoryRepository orderHistoryRepository;

    private final CartRepository cartRepository;

    private final CartItemsRepository cartItemsRepository;

    private final ProductRepository productRepository;

    public OrderController(OrderHistoryService orderHistoryService, OrderHistoryRepository orderHistoryRepository, CartRepository cartRepository, CartItemsRepository cartItemsRepository, ProductRepository productRepository)
    {
        this.orderHistoryService = orderHistoryService;
        this.orderHistoryRepository = orderHistoryRepository;
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.productRepository = productRepository;
    }


    @Authorized
    @GetMapping("/")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(name = "customer_id") int customerId
    ) throws OrderHistoryNotFoundException
    {
        Optional<List<OrderHistory>> orderHistoryList = orderHistoryService.viewAllOrderHistoryOfCustomer(customerId);
        if(orderHistoryList.isPresent())
        {
            /*
            JSONArray jsonObject = new JSONArray();
            for(OrderHistory oh : orderHistoryList.get())
            {
                // TODO change the query to get the date column
                JSONObject ohObject = new JSONObject();
                ohObject.put("id", oh.getId());
                ohObject.put("order_link", "/api/order/" + oh.getId());
                jsonObject.put(ohObject);
            }


            return ResponseEntity.ok(jsonObject);

             */
            if(orderHistoryList.get().size() == 0)
            {
                throw new OrderHistoryNotFoundException();
            }
            return ResponseEntity.ok(orderHistoryList.get());
        }
        throw new OrderHistoryNotFoundException();
    }

    @Authorized
    @GetMapping("/{id}")
    public ResponseEntity<?> viewOrder(
            @PathVariable("id") int cartId
    ) throws OrderHistoryNotFoundException
    {
        Optional<List<OrderHistory>> orderHistoryList = orderHistoryRepository.findAllByCartId(cartId);
        if(orderHistoryList.isPresent())
        {
            /*
            BigDecimal cart_cost = BigDecimal.valueOf(0.00);
            JSONObject jsonArray = new JSONObject();
            int x = 0;
            for(OrderHistory oh : orderHistoryList.get())
            {
                JSONObject purchasedItem = new JSONObject();
                purchasedItem.put("id", oh.getId());
                purchasedItem.put("customer_id", oh.getCustomerId());
                purchasedItem.put("cart_id", oh.getCartId());
                purchasedItem.put("product_id", oh.getProductId());
                purchasedItem.put("product_link", "/api/product/" + oh.getProductId());
                purchasedItem.put("name", oh.getName());
                purchasedItem.put("quantity", oh.getQuantity());
                purchasedItem.put("price", oh.getPrice());
                purchasedItem.put("total_cost", oh.getTotalCost());
                jsonArray.put(String.valueOf(x), purchasedItem);
                x++;
                cart_cost = cart_cost.add(oh.getTotalCost());
            }
            jsonArray.put("cart_cost", cart_cost);
            return ResponseEntity.ok(jsonArray);

             */
            if(orderHistoryList.get().size() == 0)
            {
                throw new OrderHistoryNotFoundException();
            }
            return ResponseEntity.ok(orderHistoryList.get());
        }
        throw new OrderHistoryNotFoundException();
    }

    //@Authorized
    @PutMapping("/add")
    public ResponseEntity<?> purchaseItems(
            @RequestParam(name = "cart_id") long cartId
    ) throws CartItemNotFoundException, NotEnoughProductQuantityException
    {
        if(!cartRepository.existsByCartId(cartId))
        {
            throw new CartItemNotFoundException();
        }
        Optional<List<CartItems>> cartItemsList = cartItemsRepository.findAllByCartId(cartId);
        List<Product> productList = productRepository.findAll();
        if(cartItemsList.isPresent())
        {
            for(CartItems cartItems : cartItemsList.get())
            {
                for(Product product : productList)
                {
                    if(cartItems.getQuantity() >= product.getQuantity())
                    {
                        throw new NotEnoughProductQuantityException();
                    }
                    cartItemsRepository.purchase_items((int) cartId);
                }
            }
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(400).body("");
    }
}
