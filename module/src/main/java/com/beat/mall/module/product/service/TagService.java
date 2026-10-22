package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.ProductTagRelation;
import com.beat.mall.module.product.entity.Tag;
import com.beat.mall.module.product.mapper.TagMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagService {

    @Resource
    private TagMapper mapper;

    @Autowired
    private ProductTagRelationService productTagRelationService;

    public Tag getById(Long id) {
        Tag entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("Tag不存在: " + id);
        }
        return entity;
    }

    public Tag extractById(Long id) {
        return mapper.extractById(id);
    }

    public Long update(Tag entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        mapper.update(entity);
        return entity.getId();
    }

    public Long insert(Tag entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setCreateTime(time);
        entity.setUpdateTime(time);
        mapper.insert(entity);
        return entity.getId();
    }

    public int delete(Long id) {
        //删除tag之前，要check是否有关联的商品，如果有，则提示不能删除！
        List<ProductTagRelation> relationList = productTagRelationService.getByTagId(id);
        if (!relationList.isEmpty()) {
            throw new RuntimeException("该Tag关联商品，不能删除");
        }

        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    public List<Tag> getAll() {
        return mapper.getAll();
    }

    //根据标签名批量查询标签（包括被删除的）
    public List<Tag> extractByNames(List<String> tagsName) {
        return mapper.extractByNames(tagsName);
    }

    //批量查询tag
    public List<Tag> getByIds(List<Long> tagIdsList) {
        return mapper.getByIds(tagIdsList);
    }
}