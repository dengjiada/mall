package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.entity.ProductTagRelation;
import com.beat.mall.module.product.entity.Tag;
import com.beat.mall.module.product.mapper.ProductMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private CategoryService categoryService;

    @Resource
    private ProductMapper productMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private ProductTagRelationService productTagRelationService;

    public Product getById(Long id) {
        return productMapper.getById(id);
    }

    public Product extractById(Long id) {
        return productMapper.extractById(id);
    }

    public int update(Product product) {
        return productMapper.update(product);
    }

    public int insert(Product product) {
        return productMapper.insert(product);
    }

    public int delete(Long productId) {
        return productMapper.delete(productId, (int) (System.currentTimeMillis() / 1000));
    }

    public List<Product> getAll() {
        return productMapper.getAll();
    }

    //解析搜索的类目名称和标签名称
    public Map<String, List<Long>> resolveSearchIds(String categoryName, String tagName) {
        Map<String, List<Long>> result = new HashMap<>();
        Set<Long> categoryIdSet = new HashSet<>();//存放类目id
        List<Long> productIds = null;//存放标签对应的产品id

        //解析类目
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            //根据类目名模糊查询类目
            List<Category> categoryList = categoryService.getByNameLike(categoryName.trim());
            if (categoryList.isEmpty()) {
                return null;//如果查不到类目，说明搜索结果为空
            }

            for (Category category : categoryList) {
                Long parentId = category.getParentId();
                if (parentId == 0) {
                    //说明是父类目，查询出其子类目
                    List<Category> children = categoryService.getChildrenByParentId(category.getId());
                    if (!children.isEmpty()) {
                        //将子类目拼接
                        children.forEach(c -> categoryIdSet.add(c.getId()));
                    }
                } else {
                    //如果是子类目，直接拼接
                    categoryIdSet.add(category.getId());
                }
            }
            //如果找了一圈，发现没有有效的类目 ID，说明搜索结果为空
            if (categoryIdSet.isEmpty()) {
                return null;
            }
        }

        //解析标签
        if (tagName != null && !tagName.trim().isEmpty()) {
            //根据标签名查询标签
            List<Tag> tagList = tagService.getByNameLike(tagName.trim());
            if (tagList.isEmpty()) {
                return null;//如果查不到该标签，说明搜索结果为空
            }
            //根据标签信息查找与之有关系的产品
            List<Long> tagIds = tagList.stream().map(Tag::getId).toList();
            List<ProductTagRelation> relationList = productTagRelationService.getByTagIds(tagIds);
            if (relationList.isEmpty()) {
                return null;//如果查不到关系，说明搜索为空
            }
            productIds = relationList.stream()
                    .map(ProductTagRelation::getProductId)
                    .distinct()
                    .toList();
        }

        List<Long> categoryIds = new ArrayList<>(categoryIdSet);
        result.put("categoryIds", categoryIds);
        result.put("productIds", productIds);
        return result;
    }

    //分页按条件查询产品
    public List<Product> pageForConsole(int page, int pageSize, String productName, List<Long> categoryIds, List<Long> productIds) {
        int offset = (page - 1) * pageSize;
        return productMapper.selectPageForConsole(offset, pageSize, productName, categoryIds, productIds);
    }

    //查询符合条件总数
    public Integer countForConsole(String productName, List<Long> categoryIds, List<Long> productIds) {
        return productMapper.selectCountForConsole(productName, categoryIds, productIds);
    }

    public int deleteByCategoryId(Long categoryId) {
        return productMapper.deleteByCategoryId(categoryId, (int) (System.currentTimeMillis() / 1000));
    }

    public List<Product> pageForApp(int page, int pageSize, String keyword) {
        int offset = (page - 1) * pageSize;
        String categoryIds = null;
        if (null != keyword && !keyword.isBlank()) {
            List<Long> ids = categoryService.getIdsByKeyword(keyword);
            if (null == ids || ids.isEmpty()) {
                categoryIds = "-1";
            } else {
                categoryIds = ids.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
            }
        }
        try {
            return productMapper.selectPageForApp(offset, pageSize, keyword, categoryIds);
        } catch (Exception e) {
            log.error("Get products fail - page: {}, pageSize: {}, keyword={}", page, pageSize, keyword, e);
            throw new RuntimeException("Get products fail");
        }
    }

    public void insertBatch(List<Product> products) {
        productMapper.insertBatch(products);
    }

    //根据产品id批量查询产品
    public List<Product> getByIds(List<Long> productIds) {
        return productMapper.getByIds(productIds);
    }
}