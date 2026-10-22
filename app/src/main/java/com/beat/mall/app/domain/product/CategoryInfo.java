package com.beat.mall.app.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryInfo {
    private String name;
    private String image;
    private Long categoryId;
    private List<Long> children;
}
