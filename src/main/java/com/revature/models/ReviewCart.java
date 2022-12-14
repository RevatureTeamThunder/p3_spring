package com.revature.models;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "review_cart")
public class ReviewCart
{
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "name")
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    public BigDecimal getTotalCost()
    {
        return totalCost;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getName()
    {
        return name;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public Integer getProductId()
    {
        return productId;
    }

    public Integer getCustomerId()
    {
        return customerId;
    }

    public Integer getCartId()
    {
        return cartId;
    }

    public long getId()
    {
        return id;
    }

    protected ReviewCart()
    {
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setCartId(Integer cartId)
    {
        this.cartId = cartId;
    }

    public void setCustomerId(Integer customerId)
    {
        this.customerId = customerId;
    }

    public void setProductId(Integer productId)
    {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
    }
}