package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.ProductTagRelation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BaseTagService {
    @Autowired
    private ProductTagRelationService productTagRelationService;

    @Autowired
    private TagService tagService;

    public int delete(Long id) {
        //删除tag之前，要check是否有关联的商品，如果有，则提示不能删除！
        List<ProductTagRelation> relationList = productTagRelationService.getByTagId(id);
        if (!relationList.isEmpty()) {
            throw new RuntimeException("该Tag关联商品，不能删除");
        }

        return tagService.delete(id);
    }
}
