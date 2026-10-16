package com.beat.mall.app.domain.product;

import com.beat.mall.app.domain.ImageInfo;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProductInfo {
    private ImageInfo image;
    private String name;
    private Float price;
    private String categoryName;
    private Long productId;
}
