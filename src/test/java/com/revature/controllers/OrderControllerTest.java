package com.revature.controllers;

import com.revature.exceptions.CartItemNotFoundException;
import com.revature.exceptions.CartNotFoundException;
import com.revature.exceptions.NotEnoughProductQuantityException;
import com.revature.exceptions.OrderHistoryNotFoundException;
import com.revature.models.Cart;
import com.revature.models.CartItems;
import com.revature.models.Product;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.PurchasedItemsRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest
{
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    static long customerID = 1;

    @Mock
    static MockHttpSession session;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchasedItemsRepository purchasedItemsRepository;

    @Test
    @Order(1)
    public void viewOrderByIdTest() throws Exception
    {

        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/1").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    public void viewOrderNotAuthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/1").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(3)
    public void viewOrderInvalidOrderIdTest() throws Exception
    {

        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(OrderHistoryNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(4)
    public void viewAllCustomerOrdersInvalidCustomerTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/?customer_id=-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(OrderHistoryNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(5)
    public void viewAllCustomerOrdersUnauthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(6)
    public void viewAllCustomerOrdersTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(7)
    public void purchaseItemsTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        // Create a cart for customer_id 2.
        Cart cart = new Cart();
        cart.setCustomerId(2);
        cart.setPurchased(false);
        Cart shoppingCart = cartRepository.save(cart);

        List<Product> randomProducts = productRepository.getTwoRandom();
        CartItems item1 = new CartItems();
        item1.setCartId(shoppingCart.getCartId());
        item1.setCustomerId(2);
        item1.setProductId((int) randomProducts.get(0).getProductId());
        item1.setQuantity(3);
        CartItems item1Result = cartItemsRepository.save(item1);

        CartItems item2 = new CartItems();
        item2.setCartId(shoppingCart.getCartId());
        item2.setCustomerId(2);
        item2.setProductId((int) randomProducts.get(1).getProductId());
        item2.setQuantity(1);
        CartItems item2Result = cartItemsRepository.save(item2);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/order/add/" + shoppingCart.getCartId()).contentType(APPLICATION_JSON_UTF8)
                .session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        cartItemsRepository.deleteById(item1Result.getId());
        cartItemsRepository.deleteById(item2Result.getId());
        purchasedItemsRepository.deleteByCartId(shoppingCart.getCartId());
        cartRepository.deleteByCartId(shoppingCart.getCartId());

        // Update the product quantities again
        productRepository.save(randomProducts.get(0));

    }

    @Test
    @Order(8)
    public void purchaseItemsCartNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/order/add/-1").contentType(APPLICATION_JSON_UTF8)
                .session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(9)
    public void purchaseItemsNotEnoughProductQuantityTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        // Create a cart for customer_id 2.
        Cart cart = new Cart();
        cart.setCustomerId(2);
        cart.setPurchased(false);
        Cart shoppingCart = cartRepository.save(cart);

        List<Product> randomProducts = productRepository.getTwoRandom();
        CartItems item1 = new CartItems();
        item1.setCartId(shoppingCart.getCartId());
        item1.setCustomerId(2);
        item1.setProductId((int) randomProducts.get(0).getProductId());
        item1.setQuantity(999999);
        CartItems item1Result = cartItemsRepository.save(item1);

        CartItems item2 = new CartItems();
        item2.setCartId(shoppingCart.getCartId());
        item2.setCustomerId(2);
        item2.setProductId((int) randomProducts.get(1).getProductId());
        item2.setQuantity(1);
        CartItems item2Result = cartItemsRepository.save(item2);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/order/add/" + shoppingCart.getCartId()).contentType(APPLICATION_JSON_UTF8)
                .session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError()));
        assertEquals(NotEnoughProductQuantityException.class, e.getCause().getClass());

        cartItemsRepository.deleteById(item1Result.getId());
        cartItemsRepository.deleteById(item2Result.getId());
        purchasedItemsRepository.deleteByCartId(shoppingCart.getCartId());
        cartRepository.deleteByCartId(shoppingCart.getCartId());
    }

    @Test
    @Order(10)
    public void purchaseItemsCartItemNotFoundExceptionTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        // Create a cart for customer_id 2.
        Cart cart = new Cart();
        cart.setCustomerId(2);
        cart.setPurchased(false);
        Cart shoppingCart = cartRepository.save(cart);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/order/add/" + shoppingCart.getCartId()).contentType(APPLICATION_JSON_UTF8)
                .session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError()));
        assertEquals(CartItemNotFoundException.class, e.getCause().getClass());

        // Cleanup
        cartRepository.deleteByCartId(shoppingCart.getCartId());

    }

}