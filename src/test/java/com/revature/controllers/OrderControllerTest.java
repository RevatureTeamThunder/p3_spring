package com.revature.controllers;

import com.revature.exceptions.OrderHistoryNotFoundException;
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

    /*
     * TODO test the purchase items function
     *  Endpoint: /api/order/add
     *  Method: PUT
     *  Request Param: cart_id
     *
     * Write a script that adds some items into a cart using JPA Repository since that does not need to be tested.
     * In the test, make sure to move the products from the purchased_items table to the cart_items table.
     * The quantity of product will also need to be adjusted so it doesn't get messed up.
     */

}