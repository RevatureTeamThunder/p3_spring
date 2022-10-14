package com.revature.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.exceptions.CartNotFoundException;
import com.revature.models.Cart;
import com.revature.models.CartItems;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import javax.naming.NoPermissionException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartControllerTest
{
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Mock
    static MockHttpSession session;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Test
    @Order(1)
    public void getCartByIdTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/1?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    public void getCartByIdUnAuthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/order/1?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(3)
    public void getCartByIdInvalidCartIdTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/-1?customer_id=1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(4)
    public void getCartByIdNoPermissionTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/1?customer_id=2").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(NoPermissionException.class, e.getCause().getClass());
    }

    @Test
    @Order(5)
    public void viewAllItemsInCartTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/view?customer_id=2").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(6)
    public void viewAllItemsInCartInvalidCustomerIdTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/view?customer_id=-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(7)
    public void viewAllItemsInCartUnauthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/view?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(8)
    public void createCartTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/create").param("customer_id", "1").session(session);

        ResultActions createCart = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        MvcResult result = createCart.andReturn();
        MockHttpServletResponse cartString = result.getResponse();
        Cart cart = new ObjectMapper().readValue(cartString.getContentAsString(), Cart.class);
        cartRepository.deleteByCartId(cart.getCartId());
    }

    @Test
    @Order(9)
    public void createCartUnauthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/create")
                .param("customer_id", "1").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(10)
    public void emptyCartTest() throws Exception
    {
        // Perform some setup data to work with
        Cart cart = new Cart();
        cart.setCustomerId(3);
        cart.setPurchased(false);

        Cart resultCart = cartRepository.save(cart);

        CartItems cartItems1 = new CartItems();
        cartItems1.setProductId(1);
        cartItems1.setQuantity(2);
        cartItems1.setCustomerId(3);
        cartItems1.setCartId(resultCart.getCartId());

        cartItemsRepository.save(cartItems1);

        CartItems cartItems2 = new CartItems();
        cartItems2.setProductId(2);
        cartItems2.setQuantity(3);
        cartItems2.setCustomerId(3);
        cartItems2.setCartId(resultCart.getCartId());

        cartItemsRepository.save(cartItems2);

        // Begin the actual test

        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/" + resultCart.getCartId() + "/empty")
                .session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @Order(11)
    public void emptyCartInvalidCartTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/-1/empty").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(12)
    public void emptyCartUnauthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/5/empty").session(session);

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
