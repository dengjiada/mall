package com.beat.mall.module.product.service;

import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.mapper.ProductMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private CategoryService categoryService;

    @Resource
    private ProductMapper productMapper;

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

    public Long edit(Long productId, Long categoryId, String images, String name, Float price, String summary) {
        if (null == categoryId) {
            throw new IllegalArgumentException("Illegal parameter(categoryId).");
        }
        if (null == images || images.equals("")) {
            throw new IllegalArgumentException("Illegal parameter(images).");
        }
        if (null == name || name.equals("")) {
            throw new IllegalArgumentException("Illegal parameter(name).");
        }
        if (null == price || price.floatValue() <= 0) {
            throw new IllegalArgumentException("Illegal parameter(price).");
        }
        if (null == summary || summary.equals("")) {
            throw new IllegalArgumentException("Illegal parameter(summary).");
        }
        Category category = categoryService.getById(categoryId);
        if (null == category) {
            throw new RuntimeException("Category not found(categoryId = " + categoryId + ").");
        }
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setImages(images);
        product.setName(name);
        product.setPrice(price);
        product.setSummary(summary);
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        product.setUpdateTime(timestamp);
        if (null == productId) {
            product.setCreateTime(timestamp);
            product.setIsDeleted(0);
            insert(product);
            return product.getId();
        } else if (null == getById(productId)) {
            throw new RuntimeException("Product(id=" + productId + ") not exist.");
        } else {
            product.setId(productId);
            update(product);
            return productId;
        }
    }

    public List<Product> getAll() {
        return productMapper.getAll();
    }

    public List<Product> pageForConsole(int page, int pageSize, String keyword) {
        int offset = (page - 1) * pageSize;
        return productMapper.selectPageForConsole(offset, pageSize, keyword);
    }

    public Integer countForConsole(String keyword) {
        return productMapper.selectCountForConsole(keyword);
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

}