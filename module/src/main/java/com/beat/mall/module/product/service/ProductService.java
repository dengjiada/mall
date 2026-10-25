package com.beat.mall.module.product.service;

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

    public List<Product> getAll() {
        return productMapper.getAll();
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