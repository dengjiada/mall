package com.beat.mall.console.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ProductVo {
    private List<String> images;
    private String name;
    private Integer saleAmount;
    private Float price;
    private String summary;
    private String createTime;
    private String updateTime;
}
