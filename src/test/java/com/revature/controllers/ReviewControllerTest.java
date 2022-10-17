package com.revature.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.exceptions.NoProductReviewException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.exceptions.ProductReviewNotFoundException;
import com.revature.models.Cart;
import com.revature.models.Product;
import com.revature.models.ProductReview;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ProductReviewRepository;
import com.revature.repositories.ProductReviewViewRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewControllerTest
{

    @Mock
    static MockHttpSession session;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductReviewViewRepository productReviewViewRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;


    @BeforeEach
    void setUp() throws Exception
    {
        session = new MockHttpSession();

        ResultActions auth = mvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"testuser@gmail.com\", \"password\": \"password\", \"role\": \"User\"}")
                .session(session));

        MvcResult mvcResult = auth.andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    @Order(1)
    void getProductReviewByReviewIdTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/3").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    void getProductReviewByIdProductReviewNotFoundTest()
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductReviewNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(3)
    void getMyProductReviewsTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/me?customer_id=1").session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    void getMyProductReviewsNoReviewsTest()
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/me?customer_id=-1").session(session);
        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductReviewNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(5)
    void getMyProductReviewsUnauthorizedTest() throws Exception
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/me?customer_id=1");

        this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Order(6)
    void getProductReviewsByProductIdTest() throws Exception
    {

        Product productList = productRepository.findByReviewCountGreaterThan(0);
        int productId = (int) productList.getProductId();

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/view/" + productId).session(session);

        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(7)
    void getProductReviewsByProductIdProductNotFoundTest()
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/view/-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(8)
    void getProductReviewsByProductIdNoReviewsTest()
    {
        Product product = new Product();
        product.setName("Tree Waster");
        product.setQuantity(999);
        product.setReviewCount(0);
        product.setRating(0);
        product.setImage("");
        product.setDescription("A useless sheet to track credit applications");
        product.setCategoryId(3);
        product.setPrice(0.01);

        Product newProduct = productRepository.save(product);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.get("/api/review/view/" + (int) newProduct.getProductId()).session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(NoProductReviewException.class, e.getCause().getClass());

        productRepository.deleteByProductId(newProduct.getProductId());
    }

    @Test
    @Order(9)
    void updateReview() throws Exception
    {
        // First create a review. Afterwards, update it

        List<Product> productList = productRepository.getTwoRandom();

        ProductReview productReview = new ProductReview();
        productReview.setProductId((int) productList.get(0).getProductId());
        productReview.setCustomerId(1);
        productReview.setRating(4);
        productReview.setProductComments("Test comment");
        ProductReview newReview = productReviewRepository.save(productReview);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/review/update/" + (int) newReview.getReviewId()+ "?" +
                "customer_id=1&rating=5&comment=Test%20comment").session(session);

        ResultActions resultActions = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult result = resultActions.andReturn();
        MockHttpServletResponse reviewString = result.getResponse();
        ProductReview productReview1 = new ObjectMapper().readValue(reviewString.getContentAsString(), ProductReview.class);

        assertEquals(5, productReview1.getRating());
      //  productReviewRepository.deleteByReviewId(newReview.getReviewId());
        productReviewRepository.delete_rating(productReview.getProductId(), 1, 0, "delete");

    }

    @Test
    @Order(10)
    void updateProductReviewNoPermissionTest()
    {
        // First create a review. Afterwards, update it

        List<Product> productList = productRepository.getTwoRandom();

        ProductReview productReview = new ProductReview();
        productReview.setProductId((int) productList.get(0).getProductId());
        productReview.setCustomerId(1);
        productReview.setRating(4);
        productReview.setProductComments("Test comment");
        ProductReview newReview = productReviewRepository.save(productReview);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/review/update/" + (int) newReview.getReviewId()+ "?" +
                "customer_id=-1&rating=5&comment=Test%20comment").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(NoPermissionException.class, e.getCause().getClass());

        productReviewRepository.deleteByReviewId(newReview.getReviewId());
    }

    @Test
    @Order(11)
    void updateProductReviewProductReviewNotFoundTest()
    {
        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/review/update/-1?" +
                "customer_id=1&rating=5&comment=Test%20comment").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductReviewNotFoundException.class, e.getCause().getClass());
    }

    @Test
    @Order(12)
    void deleteReview() throws Exception
    {

        List<Product> productList = productRepository.getTwoRandom();

        ProductReview productReview = new ProductReview();
        productReview.setProductId((int) productList.get(0).getProductId());
        productReview.setCustomerId(1);
        productReview.setRating(4);
        productReview.setProductComments("Test comment");
        ProductReview newReview = productReviewRepository.save(productReview);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/review/delete/" + newReview.getReviewId() + "?customer_id=1").session(session);
        this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @Order(13)
    void deleteReviewNoPermissionTest()
    {
        List<Product> productList = productRepository.getTwoRandom();

        ProductReview productReview = new ProductReview();
        productReview.setProductId((int) productList.get(0).getProductId());
        productReview.setCustomerId(1);
        productReview.setRating(4);
        productReview.setProductComments("Test comment");
        ProductReview newReview = productReviewRepository.save(productReview);

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.delete("/api/review/delete/" + newReview.getReviewId() + "?customer_id=-1").session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(NoPermissionException.class, e.getCause().getClass());
    }

    @Test
    @Order(14)
    void AddReviewTest() throws Exception
    {
        List<Product> productList = productRepository.getTwoRandom();
        JSONObject productReviewJSON = new JSONObject();
        productReviewJSON.put("product_id", productList.get(0).getProductId());
        productReviewJSON.put("customer_id", 1);
        productReviewJSON.put("rating", 5);
        productReviewJSON.put("comment", "Test");

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/review/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productReviewJSON.toString()).session(session);

        ResultActions resultActions = this.mvc.perform(getOrderRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse reviewString = result.getResponse();
        ProductReview productReview1 = new ObjectMapper().readValue(reviewString.getContentAsString(), ProductReview.class);

        assertEquals(5, productReview1.getRating());
        productReviewRepository.deleteByReviewId(productReview1.getReviewId());
    }

    @Test
    @Order(15)
    void AddReviewProductNotFoundTest() throws Exception
    {
        JSONObject productReviewJSON = new JSONObject();
        productReviewJSON.put("product_id", -1);
        productReviewJSON.put("customer_id", 1);
        productReviewJSON.put("rating", 5);
        productReviewJSON.put("comment", "Test");

        RequestBuilder getOrderRequest = MockMvcRequestBuilders.put("/api/review/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productReviewJSON.toString()).session(session);

        Exception e = assertThrows(NestedServletException.class, () ->
                this.mvc.perform(getOrderRequest).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError()));
        assertEquals(ProductNotFoundException.class, e.getCause().getClass());
    }
}