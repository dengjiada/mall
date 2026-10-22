package com.beat.mall.console.controller.product;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.product.CategoryInfo;
import com.beat.mall.console.domain.product.CategoryListVo;
import com.beat.mall.console.domain.product.CategoryVo;
import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.service.CategoryService;
import com.beat.mall.module.product.service.ProductService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @Autowired
    private ProductService productService;

    /**
     * 详情接口
     */
    @RequestMapping("/info")
    public Response getDetail(@VerifiedUser User loginUser, @RequestParam(name = "categoryId") Long categoryId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Category category = service.getById(categoryId);
        List<Long> children = new ArrayList<>();
        Long parentId = category.getParentId();
        if (parentId == 0) {
            //说明是顶级类目，将类目id当作条件查询其子类目
            List<Category> childrenList = service.getChildrenByParentId(category.getId());
            children = childrenList.stream()
                    .map(Category::getId)
                    .toList();
        }

        CategoryVo vo = new CategoryVo();
        vo.setName(category.getName());
        vo.setImage(category.getImage());
        vo.setChildren(children);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sdf.format(category.getCreateTime() * 1000L);
        String updateTime = sdf.format(category.getUpdateTime() * 1000L);
        vo.setCreateTime(createTime);
        vo.setUpdateTime(updateTime);
        return new Response(1001, vo);
    }

    /**
     * 列表接口
     */
    @RequestMapping("/list")
    public Response getAll(@VerifiedUser User loginUser) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
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

    /**
     * 新增接口
     */
    @RequestMapping("/create")
    public Response create(@VerifiedUser User loginUser,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "image") String image,
                           @RequestParam(name = "parentId") Long parentId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Category category = new Category();
        category.setName(name);
        category.setImage(image);
        category.setParentId(parentId);
        try {
            service.insert(category);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Create category fail.", e);
            return new Response(3003);
        }
    }

    /**
     * 更新接口
     */
    @RequestMapping("/update")
    public Response update(@VerifiedUser User loginUser,
                           @RequestParam(name = "categoryId") Long categoryId,
                           @RequestParam(name = "parentId") Long parentId,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "image") String image) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setParentId(parentId);
        category.setName(name);
        category.setImage(image);
        try {
            service.update(category);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Update category fail.", e);
            return new Response(3004);
        }
    }

    /**
     * 删除接口
     */
    @RequestMapping("/delete")
    public Response delete(@VerifiedUser User loginUser, @RequestParam(name = "categoryId") Long categoryId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        try {
            Category category = service.getById(categoryId);
            Long parentId = category.getParentId();
            if (parentId == 0) {
                //说明是顶级类目，将类目id当作条件查询其子类目
                List<Category> childrenList = service.getChildrenByParentId(category.getId());
                if (!BaseUtil.isEmpty(childrenList)) {
                    //如果子类目不为空，提示不能删除
                    return new Response(3011);
                }
            }
            productService.deleteByCategoryId(categoryId);
            service.delete(categoryId);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Delete category fail.", e);
            return new Response(3005);
        }
    }

}