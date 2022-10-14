package com.revature.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import com.revature.models.Customer;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTests {
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	static long customerID = 0;
	
	@Mock
	static MockHttpSession session;
	
    @Autowired
    private MockMvc mvc;
    
    @Test
    @Order(1)
    public void loginFailedTest() throws Exception{
    	System.out.println("1- LoginFailed");
    	session = new MockHttpSession();
    	mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"erroneous_email\", \"password\" : \"problematic_password\", \"role\" : \"random_role\"}")
			.session(session))
			.andExpect(status().isBadRequest());
    	assert(session.getAttribute("user") == null);
    }
    
    @Test
    @Order(2)
    public void loginSuccessTest() throws Exception{
    	System.out.println("2- LoginSuccess");
    	session = new MockHttpSession(); //For some reason the MockHttpSession doesn't persist between tests
    	mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"testuser@gmail.com\", \"password\" : \"password\", \"role\" : \"User\"}")
			.session(session))
			.andExpect(status().isOk());
    	assert(((Customer)session.getAttribute("user")).getFirstName().equals("Test"));
    }
    
    @Test
    @Order(3)
    public void logoutTest() throws Exception{
    	System.out.println("3- Logout");
		mvc.perform(MockMvcRequestBuilders.post("/auth/logout")
			.session(session))
			.andExpect(status().isOk());
    	assert(session.getAttribute("user") == null);
    }
    
    @Test
    @Order(4)
    public void registerDuplicateEmailTest() throws Exception{
    	System.out.println("4- RegisterDuplicate");
    	assertThrows(NestedServletException.class, () -> {
		mvc.perform(MockMvcRequestBuilders.post("/auth/register")
			.contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"testuser@gmail.com\", \"password\" : \"testpass\", \"firstName\" : \"Foo\", \"lastName\" : \"Bar\"}"))
			.andExpect(status().isOk());
    	});
    }
    
    @Test
    @Order(5)
    public void registerSuccessTest() throws Exception{
    	System.out.println("5- RegisterSuccess");
		mvc.perform(MockMvcRequestBuilders.post("/auth/register")
			.contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"RegisterTest@junit.test\", \"password\" : \"testpass\", \"firstName\" : \"Foo\", \"lastName\" : \"Bar\"}"))
			.andExpect(status().is(201));
    }
    
    @Test
    @Order(6)
    public void loginNewUserTest() throws Exception{
    	System.out.println("6- LoginNew");
    	session = new MockHttpSession();
    	Cookie[] cookies = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"RegisterTest@junit.test\", \"password\" : \"testpass\", \"role\" : \"User\"}")
			.session(session))
			.andExpect(status().isOk())
			.andReturn().getResponse().getCookies();
    	System.out.println("There are "+cookies.length+" cookies");
    	for(Cookie c : cookies) {
    		System.out.println("Cookie: "+c.getName());
    	}
		Assertions.assertEquals("Foo", ((Customer)session.getAttribute("user")).getFirstName());
		customerID = ((Customer)session.getAttribute("user")).getCustomerId();
		System.out.println("New user ID = " + customerID);
    }
    
    @Test
    @Order(7)
    public void deleteNewUserTest() throws Exception{
    	System.out.println("7- Delete");
    	mvc.perform(MockMvcRequestBuilders.delete("/auth/delete/"+(long)customerID))
    		.andExpect(status().is(204));
    }
    @Test
    @Order(8)
    public void loginDeletedUserTest() throws Exception{
    	System.out.println("8- LoginDeleted");
    	mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
			.content("{\"email\" : \"RegisterTest@junit.test\", \"password\" : \"testpass\", \"role\" : \"User\"}"))
			.andExpect(status().isBadRequest());
    }
}