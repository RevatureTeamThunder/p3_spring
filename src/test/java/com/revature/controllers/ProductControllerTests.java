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
import org.springframework.boot.test.system.OutputCaptureExtension;
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
    public void viewProductsTest() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/api/product/").session(getSession()))
        	.andExpect(status().isOk())
        	.andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"Tennis Ball Chew Toy\",\"description\":\"Heavy duty and made with durable materials so it will last a long time\"")));
    }
    
    @Test
    public void searchNoResultsTest() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "sgstdfklskjdnfkk"))
    		.andExpect(status().isOk())
    		.andExpect(content().string(equalTo("[]")));
    }

    @Test
    public void searchByNameTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "name"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by name");
        System.out.println(result);
    }
    
    @Test
    public void searchByPriceTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "price"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by price");
        System.out.println(result);
    }
    
    @Test
    public void searchByPriceDefaultTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "geariushg"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by default");
        System.out.println(result);
    }

    @Test
    public void searchByReviewTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "review"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by review");
        System.out.println(result);
    }

    @Test
    public void searchByCategoryTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "category"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by category");
        System.out.println(result);
    }

    @Test
    public void searchByQuantityTest() throws Exception
    {
        String result = mvc.perform(MockMvcRequestBuilders.get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "quantity"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by quantity");
        System.out.println(result);
    }
    
    @Test
    public void getByIdTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/api/product/2").session(getSession()))
    		.andExpect(status().isOk())
    		.andExpect(content().string(org.hamcrest.Matchers.containsString("Lamb Flavored Dry Dog Food")));
    }
    
    //@Test
    @Order(1)
    public void putProductTest() throws Exception{
        String result = mvc.perform(MockMvcRequestBuilders.put("/api/product").session(getSession())
        	.contentType(APPLICATION_JSON_UTF8)
        	.content("{'name':'TestProduct','description':'Testing functionality of uploading and deleting products','quantity':10,'price':50,'image':'','rating':3,'review_count':5,'category_id':1}"))
        	.andExpect(status().is(201))
        	.andReturn().getResponse().getContentAsString();
        System.out.println("Test 5 result: "+result);
        //Set createdProductId here based on what exactly is returned
    }

    //@Test
    @Order(2)
    public void deleteProductTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/api/product/"+createdProductId).session(getSession()))
    		.andExpect(status().isOk());
    }
    
    //@Test
    @Order(3)
    public void deleteProductFailedTest() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/api/product/"+createdProductId).session(getSession()))
    		.andExpect(status().is(404));
    }
}