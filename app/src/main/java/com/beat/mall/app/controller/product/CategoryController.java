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
        List<CategoryInfo> categoryInfoList = new ArrayList<>();
        for (Category category : categoryList) {
            CategoryInfo categoryInfo = new CategoryInfo();
            Long parentId = category.getParentId();
            if (parentId == 0) {
                //说明是顶级类目，将类目id当作条件查询其子类目
                List<Category> children = service.getChildrenByParentId(category.getId());
                List<Long> childrenList = children.stream()
                        .map(Category::getId)
                        .toList();
                categoryInfo.setChildren(childrenList);
            } else {
                //说明是子类目
                categoryInfo.setChildren(new ArrayList<>());
            }
            categoryInfo.setName(category.getName())
                    .setImage(category.getImage())
                    .setCategoryId(category.getId());

            categoryInfoList.add(categoryInfo);
        }
        CategoryListVo vo = new CategoryListVo();
        vo.setList(categoryInfoList);
        return new Response(1001, vo);
    }

}