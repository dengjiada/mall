package com.beat.mall.app.controller.product;

import com.beat.mall.app.domain.product.CategoryInfo;
import com.beat.mall.app.domain.product.CategoryListVo;
import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.service.CategoryService;
import com.beat.mall.utils.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService service;

    /**
     * 列表接口
     */
    @RequestMapping("/list")
    public Response getAll() {
        List<Category> categoryList = service.getAll();
        List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
        categoryList.stream().forEach(category -> {
            categoryInfoList.add(new CategoryInfo().setName(category.getName())
                    .setImage(category.getImage())
                    .setCategoryId(category.getId()));
        });
        CategoryListVo vo = new CategoryListVo();
        vo.setList(categoryInfoList);
        return new Response(1001, vo);
    }

}