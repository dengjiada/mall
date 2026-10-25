package com.beat.mall.console.controller.product;

import com.beat.mall.module.product.entity.Tag;
import com.beat.mall.module.product.service.BaseTagService;
import com.beat.mall.module.product.service.TagService;
import com.beat.mall.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService service;

    @Autowired
    private BaseTagService baseTagService;

    /**
    * 详情接口
    */
    @RequestMapping("/info")
    public Tag getDetail(@RequestParam(name = "id") Long id) {
        return service.getById(id);
    }

    /**
    * 列表接口
    */
    @RequestMapping("/list")
    public List<Tag> getAll() {
        return service.getAll();
    }

    /**
    * 新增接口
    */
    @PostMapping("/create")
    public void create(@RequestBody Tag entity) {
        service.insert(entity);
    }

    /**
    * 更新接口
    */
    @PutMapping("/update")
    public void update(@RequestBody Tag entity) {
        service.update(entity);
    }

    /**
    * 删除接口
    */
    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        try {
            baseTagService.delete(id);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Delete tag fail.", e);
            return new Response(3009);
        }
    }

}