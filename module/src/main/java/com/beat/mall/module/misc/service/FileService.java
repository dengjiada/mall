package com.beat.mall.module.misc.service;

import com.beat.mall.module.misc.entity.File;
import com.beat.mall.module.misc.mapper.FileMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileService {

    @Resource
    private FileMapper mapper;

    public File getById(Long id) {
        File entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("File不存在: " + id);
        }
        return entity;
    }

    public File extractById(Long id) {
        return mapper.extractById(id);
    }

    public Long update(File entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        mapper.update(entity);
        return entity.getId();
    }

    public Long insert(File entity) {
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

    public List<File> getAll() {
        return mapper.getAll();
    }
}