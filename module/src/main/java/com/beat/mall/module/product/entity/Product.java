package com.beat.mall.module.product.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Product {
    private Long id;
    private Long categoryId;
    private String images;
    private String name;
    private Float price;
    private String summary;
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000);
    private Integer createTime;
    private Integer isDeleted;
}
