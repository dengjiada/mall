package com.beat.mall.app.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryInfo {
    private String name;
    private String image;
    private Long categoryId;
}
