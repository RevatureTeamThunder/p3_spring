package com.revature.controllers;

import java.nio.charset.Charset;

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
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartControllerTests {
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
    @Autowired
    public MockMvc mvc;
	
	@Mock
	static MockHttpSession session;
    
	@BeforeTestClass
	public void setup() {
		try {
			session = (MockHttpSession) mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
					.content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
					.session(new MockHttpSession())).andReturn().getRequest().getSession();
		} catch(Exception e) {
			System.out.println("Exception thrown while trying to initialize exception");
			System.out.println("Exception stack trace:");
			e.printStackTrace();
		}
	}
	
    @Test
    @Order(1)
    public void viewOrderByIdTest() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/api/order/1").session(session))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    } 
}
