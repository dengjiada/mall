package com.beat.mall.console.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryVo {
    private String name;
    private String image;
    private List<Long> children;
    private String createTime;
    private String updateTime;
}
