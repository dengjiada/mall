package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.Tag;
import com.beat.mall.module.product.mapper.TagMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagService {

    @Resource
    private TagMapper mapper;

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

    //根据标签名查询标签
    public List<Tag> getByNameLike(String name) {
        return mapper.getByNameLike(name);
    }
}