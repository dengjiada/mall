package com.beat.mall.console.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProductInfo {
    private String image;
    private String name;
    private Float price;
    private Long productId;
}
