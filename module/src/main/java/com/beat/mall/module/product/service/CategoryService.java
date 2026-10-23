package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.mapper.CategoryMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryService {

    @Resource
    private CategoryMapper mapper;

    public Category getById(Long id) {
        Category entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("Category不存在: " + id);
        }
        return entity;
    }

    public Category extractById(Long id) {
        return mapper.extractById(id);
    }

    public Long update(Category entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        mapper.update(entity);
        return entity.getId();
    }

    public Long insert(Category entity) {
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

    public List<Category> getAll() {
        return mapper.getAll();
    }

    public List<Long> getIdsByKeyword(String keyword) {
        return mapper.getIdsByKeyword(keyword);
    }

    //根据parentId查询子类目
    public List<Category> getChildrenByParentId(Long parentId) {
        return mapper.getChildrenByParentId(parentId);
    }

    //根据类目名查询类目
    public List<Category> getByNameLike(String name) {
        return mapper.getByNameLike(name);
    }
}