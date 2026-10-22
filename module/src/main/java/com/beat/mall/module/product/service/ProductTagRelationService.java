package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.ProductTagRelation;
import com.beat.mall.module.product.mapper.ProductTagRelationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductTagRelationService {

    @Resource
    private ProductTagRelationMapper mapper;

    public ProductTagRelation getById(Long id) {
        ProductTagRelation entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("ProductTagRelation不存在: " + id);
        }
        return entity;
    }

    public ProductTagRelation extractById(Long id) {
        return mapper.extractById(id);
    }

    public Long update(ProductTagRelation entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        mapper.update(entity);
        return entity.getId();
    }

    public Long insert(ProductTagRelation entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setCreateTime(time);
        entity.setUpdateTime(time);
        mapper.insert(entity);
        return entity.getId();
    }

    public int delete(Long id) {
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    public List<ProductTagRelation> getAll() {
        return mapper.getAll();
    }

    //批量新增
    public void insertBatch(List<ProductTagRelation> toInsertList) {
        mapper.insertBatch(toInsertList);
    }

    //批量修改
    public void updateBatch(List<ProductTagRelation> toUpdateList) {
        mapper.updateBatch(toUpdateList);
    }

    //根据产品id查询关系（包括被删除的）
    public List<ProductTagRelation> extractByProductId(Long productId) {
        return mapper.extractByProductId(productId);
    }

    //根据标签id查询关系
    public List<ProductTagRelation> getByTagId(Long tagId) {
        return mapper.getByTagId(tagId);
    }

    //根据产品id查找关系（未删除的）
    public List<ProductTagRelation> getByProductId(Long productId) {
        return mapper.getByProductId(productId);
    }
}