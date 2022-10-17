package com.revature.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTests {
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
    @Autowired
    public MockMvc mvc;
    
    public static int createdProductId = 0;

	//Can't get the session to persist
	/*@BeforeTestClass
	@Mock
	static MockHttpSession session;
    
	public void setup() {
		try {

			session = new MockHttpSession();
	        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
	                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
	                .session(session));
	        MvcResult mvcResult = auth.andReturn();
	        session = (MockHttpSession) mvcResult.getRequest().getSession();
	        
		} catch(Exception e) {
			System.out.println("Exception thrown while trying to initialize exception");
			System.out.println("Exception stack trace:");
			e.printStackTrace();
		}
	}*/
	
	private MockHttpSession getSession() throws Exception {

		MockHttpSession s1 = new MockHttpSession();
        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(APPLICATION_JSON_UTF8)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(s1));
        MvcResult mvcResult = auth.andReturn();
        s1 = (MockHttpSession) mvcResult.getRequest().getSession();
        return s1;
	}
	
    @Test
    @Order(1)
    public void viewProductsTest() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/api/product/").session(getSession()))
        	.andExpect(status().isOk())
        	.andExpect(content().string(org.hamcrest.Matchers.containsString("\"productId\":4,\"name\":\"Baseball Cap\",\"description\":\"A fancy baseball cap for a fancy person\"")));
    }
    
    @Test
    @Order(2)
    public void searchNoResultsTest() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "sgstdfklskjdnfkk"))
    		.andExpect(status().isOk())
    		.andExpect(content().string(equalTo("[]")));
        
    }
    
    @Test
    @Order(3)
    public void searchSuccessTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "bag")
        	.param("order_by", "name"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String[] resultSplit = result.split("[:,]");
        Assertions.assertEquals("\"Garbage Bags\"", resultSplit[3]);
        
    }
    
    @Test
    @Order(4)
    public void getByIdTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/api/product/2").session(getSession()))
    		.andExpect(status().isOk())
    		.andExpect(content().string(org.hamcrest.Matchers.containsString("\"productId\":2,\"name\":\"TeeShirt\",\"description\":\"A nice TeeShirt\"")));
    }
    
    /*@Test
    @Order(5)
    public void putProductTest() throws Exception{
        String result = mvc.perform(MockMvcRequestBuilders.put("/api/product").session(getSession())
        	.contentType(APPLICATION_JSON_UTF8)
        	.content("{\"name\":\"TestProduct\",\"description\":\"Testing functionality of uploading and deleting products\",\"quantity\":10,\"price\":50,\"image\":\"\",\"rating\":3,\"review_count\":5,\"category_id\":1}"))
        	.andExpect(status().is(201))
        	.andReturn().getResponse().getContentAsString();
        System.out.println("Test 5 result: "+result);
        //Set createdProductId here based on what exactly is returned
    }

    @Test
    @Order(6)
    public void deleteProductTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/api/product/"+createdProductId).session(getSession()))
    		.andExpect(status().isOk());
    }
    
    @Test
    @Order(7)
    public void deleteProductFailedTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/api/product/"+createdProductId).session(getSession()))
    		.andExpect(status().is(404));
    }*/
}