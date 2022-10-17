package com.revature.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddReviewRequest
{
    private Integer product_id;
    private Integer customer_id;
    private Integer rating;
    private String comment;

    public Integer getProduct_id()
    {
        return product_id;
    }

    public void setProduct_id(Integer product_id)
    {
        this.product_id = product_id;
    }

    public Integer getCustomer_id()
    {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id)
    {
        this.customer_id = customer_id;
    }

    public Integer getRating()
    {
        return rating;
    }

    public void setRating(Integer rating)
    {
        this.rating = rating;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
