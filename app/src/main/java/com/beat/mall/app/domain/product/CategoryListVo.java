package com.beat.mall.app.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryListVo {
    private List<CategoryInfo> list;
}
