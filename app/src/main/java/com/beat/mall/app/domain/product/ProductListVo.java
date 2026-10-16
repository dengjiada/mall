package com.beat.mall.app.domain.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ProductListVo {
    private List<ProductInfo> list;
    private Boolean isEnd;
}
