package com.revature.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.exceptions.CartNotFoundException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.Cart;
import com.revature.models.CartItems;
import com.revature.models.Product;
import com.revature.models.ProductReview;
import com.revature.repositories.CartItemsRepository;
import com.revature.repositories.CartRepository;
import com.revature.repositories.ProductRepository;
import org.checkerframework.checker.units.qual.C;
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
import java.util.List;

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

    @Autowired
    private ProductRepository productRepository;

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

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/view?customer_id=2").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        cartItemsRepository.deleteById(item1Result.getId());
        cartItemsRepository.deleteById(item2Result.getId());
        cartRepository.deleteByCartId(shoppingCart.getCartId());
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

    @Test
    @Order(13)
    public void getCartByCustomerIdTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/customer_id/1").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(14)
    void getCartByCustomerIdCartNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();



        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/cart/customer_id/-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());

    }

    @Test
    @Order(15)
    void addItemToCartTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/4/add?product_id=32&quantity=1").session(session);

        ResultActions resultActions = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MvcResult addResult = resultActions.andReturn();

        MockHttpServletResponse cartString = addResult.getResponse();
        CartItems cartItems = new ObjectMapper().readValue(cartString.getContentAsString(), CartItems.class);
        cartItemsRepository.deleteById(cartItems.getId());
    }

    @Test
    @Order(16)
    void addItemToCartProductNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/4/add?product_id=-1&quantity=1").session(session);
        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductNotFoundException.class, e.getCause().getClass());

    }

    @Test
    @Order(17)
    void addItemToCartCartNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/-1/add?product_id=32&quantity=1")
                .session(session);
        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(18)
    void deleteItemFromCartTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        CartItems cartItems = new CartItems();
        cartItems.setQuantity(1);
        cartItems.setProductId(32);
        cartItems.setCustomerId(2);
        cartItems.setCartId(4L);
        CartItems newItem = cartItemsRepository.save(cartItems);


        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/4/delete?product_id=32")
                .session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

    @Test
    @Order(19)
    void deleteItemFromCartCartNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        CartItems cartItems = new CartItems();
        cartItems.setQuantity(1);
        cartItems.setProductId(32);
        cartItems.setCustomerId(2);
        cartItems.setCartId(4L);
        CartItems newItem = cartItemsRepository.save(cartItems);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/-1/delete?product_id=32").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(CartNotFoundException.class, e.getCause().getClass());

        cartItemsRepository.deleteById(newItem.getId());
    }

    @Test
    @Order(20)
    void deleteItemFromCartProductNotFoundTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        CartItems cartItems = new CartItems();
        cartItems.setQuantity(1);
        cartItems.setProductId(32);
        cartItems.setCustomerId(2);
        cartItems.setCartId(4L);
        CartItems newItem = cartItemsRepository.save(cartItems);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/cart/4/delete?product_id=-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductNotFoundException.class, e.getCause().getClass());

        cartItemsRepository.deleteById(newItem.getId());
    }

    @Test
    @Order(21)
    void updateItemQuantityTest() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();

        CartItems cartItems = new CartItems();
        cartItems.setQuantity(1);
        cartItems.setProductId(32);
        cartItems.setCustomerId(2);
        cartItems.setCartId(4L);
        CartItems newItem = cartItemsRepository.save(cartItems);


        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/cart/4/update?product_id=32&quantity=2")
                .session(session);

        ResultActions resultActions = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse cartString = result.getResponse();
        CartItems cartItems1 = new ObjectMapper().readValue(cartString.getContentAsString(), CartItems.class);
        assertEquals(2, cartItems1.getQuantity());
        cartItemsRepository.deleteById(newItem.getId());
    }
}
