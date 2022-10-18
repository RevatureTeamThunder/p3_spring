package com.revature.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.Product;
import com.revature.models.ProductReview;
import com.revature.repositories.ProductRepository;
import org.json.JSONObject;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTests {
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
    @Autowired
    public MockMvc mvc;
    
    public static int createdProductId = 0;

    @Autowired
    private ProductRepository productRepository;
	
	private MockHttpSession getSession() throws Exception {

		MockHttpSession s1 = new MockHttpSession();
        ResultActions auth = mvc.perform(post("/auth/login").contentType(APPLICATION_JSON_UTF8)
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
        mvc.perform(get("/api/product/").session(getSession()))
        	.andExpect(status().isOk())
        	.andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"Tennis Ball Chew Toy\",\"description\":\"Heavy duty and made with durable materials so it will last a long time\"")));
    }
    
    @Test
    @Order(2)
    public void searchNoResultsTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/product/search")
                .param("name", "sgstdfklskjdnfkk")
                .session(getSession());

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(3)
    public void searchByNameTest() throws Exception
    {
        mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "name"))
                .andDo(MockMvcResultHandlers.print())
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    }
    
    @Test
    @Order(4)
    public void searchByPriceTest() throws Exception
    {
        String result = mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "price"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by price");
        System.out.println(result);
    }
    
    @Test
    @Order(5)
    public void searchByPriceDefaultTest() throws Exception
    {
        String result = mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "geariushg"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by default");
        System.out.println(result);
    }

    @Test
    @Order(6)
    public void searchByReviewTest() throws Exception
    {
        String result = mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "review"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by review");
        System.out.println(result);
    }

    @Test
    @Order(7)
    public void searchByCategoryTest() throws Exception
    {
        String result = mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "category"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by category");
        System.out.println(result);
    }

    @Test
    @Order(8)
    public void searchByQuantityTest() throws Exception
    {
        String result = mvc.perform(get("/api/product/search").session(getSession())
        	.param("name", "chew")
        	.param("order_by", "quantity"))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        System.out.println("Product controller by quantity");
        System.out.println(result);
    }
    
    @Test
    @Order(9)
    public void getByIdTest() throws Exception{
        mvc.perform(get("/api/product/2").session(getSession()))
    		.andExpect(status().isOk())
    		.andExpect(content().string(org.hamcrest.Matchers.containsString("Lamb Flavored Dry Dog Food")));
    }

    @Test
    @Order(10)
    public void putProductTest() throws Exception{
        JSONObject productJson = new JSONObject();
        productJson.put("name", "TestProduct");
        productJson.put("description", "Testing functionality of uploading products");
        productJson.put("quantity", 10);
        productJson.put("price", 50);
        productJson.put("image", "");
        productJson.put("rating", 0);
        productJson.put("reviewCount", 0);
        productJson.put("categoryId", 1);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson.toString()).session(getSession());

        ResultActions resultActions = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse productString = result.getResponse();
        Product product = new ObjectMapper().readValue(productString.getContentAsString(), Product.class);

        productRepository.deleteByProductId(product.getProductId());
    }

    @Test
    @Order(11)
    public void deleteProductTest() throws Exception{

        Product product = new Product();
        product.setName("TestProduct");
        product.setDescription("Testing functionality of uploading products");
        product.setQuantity(10);
        product.setPrice(50);
        product.setImage("");
        product.setRating(0);
        product.setReviewCount(0);
        product.setCategoryId(1);

        Product newProduct = productRepository.save(product);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/product/" + newProduct.getProductId())
                .session(getSession());

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        productRepository.deleteByProductId(newProduct.getProductId());
    }
    
    @Test
    @Order(12)
    public void deleteProductFailedTest() throws Exception{
        mvc.perform(delete("/api/product/-1").session(getSession()))
    		.andExpect(status().is(404));
    }
}