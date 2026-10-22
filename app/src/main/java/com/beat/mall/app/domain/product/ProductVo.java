package com.beat.mall.app.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ProductVo {
    private List<String> images;
    private List<String> tags;
    private String name;
    private Integer saleAmount;
    private Float price;
    private String summary;
    private String categoryName;
    private String categoryImage;
}
