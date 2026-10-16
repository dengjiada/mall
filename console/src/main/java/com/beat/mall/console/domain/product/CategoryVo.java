package com.beat.mall.console.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryVo {
    private String name;
    private String image;
    private String createTime;
    private String updateTime;
}
