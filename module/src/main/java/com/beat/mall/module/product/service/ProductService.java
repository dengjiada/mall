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

    @Transactional(rollbackFor = Exception.class)
    public Long edit(Long productId, Long categoryId, String images, String name, Float price, String summary, String tags) {
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
        if (null == tags || tags.equals("")) {
            throw new IllegalArgumentException("Illegal parameter(tags).");
        }
        Category category = categoryService.getById(categoryId);
        if (null == category) {
            throw new RuntimeException("Category not found(categoryId = " + categoryId + ").");
        }
        //按照$分离tags，拿到标签名
        String[] tagsName = tags.split("\\$");
        //收集标签id
        List<Long> tagIds = new ArrayList<>();
        //根据标签名批量查询标签（包括被删除的）
        List<Tag> tagsList = tagService.extractByNames(Arrays.asList(tagsName));
        Map<String, Tag> tagsMap = tagsList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Tag::getName, tag -> tag));
        //遍历处理
        for (String tagName : tagsName) {
            Tag tag = tagsMap.get(tagName);
            if (tag == null) {
                //如果tag不存在，插入该tag
                Long tagId = tagService.insert(new Tag().setName(tagName));
                tagIds.add(tagId);
            } else if (tag.getIsDeleted() == 1) {
                //如果tag存在，但被删除了，先将tag更新回来，再取id
                tag.setIsDeleted(0);
                Long tagId = tagService.update(tag);
                tagIds.add(tagId);
            } else {
                //如果tag存在，且未被删除，取id
                tagIds.add(tag.getId());
            }
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
            productId = product.getId();
        } else {
            Product existProduct = getById(productId);
            if (existProduct == null) {
                throw new RuntimeException("Product(id=" + productId + ") not exist.");
            }
            product.setId(productId);
            update(product);
        }
        //建⽴product 和 标签的关系
        //根据productId查询与tag的关系（包括被删除的）
        List<ProductTagRelation> relationList = productTagRelationService.extractByProductId(productId);
        //转成map
        Map<Long, ProductTagRelation> relationMap = relationList.stream()
                .collect(Collectors.toMap(ProductTagRelation::getTagId, relation -> relation));
        //用于批量插入和批量修改的list
        List<ProductTagRelation> toInsertList = new ArrayList<>();
        List<ProductTagRelation> toUpdateList = new ArrayList<>();
        for (Long tagId : tagIds) {
            ProductTagRelation productTagRelation = relationMap.get(tagId);
            if (productTagRelation == null) {
                //如果未查询到，添加到insert的list中
                ProductTagRelation relation = new ProductTagRelation().setProductId(productId)
                        .setTagId(tagId)
                        .setCreateTime(timestamp)
                        .setUpdateTime(timestamp)
                        .setIsDeleted(0);
                toInsertList.add(relation);
            } else if (productTagRelation.getIsDeleted() == 1) {
                //如果有关系，但被删除了，添加到update的list中
                productTagRelation.setUpdateTime(timestamp)
                        .setIsDeleted(0);
                toUpdateList.add(productTagRelation);
            }
            //如果有关系，且关系正常，保留即可
            //从 map 中移除处理过的，剩下的就是这次被取消绑定的
            relationMap.remove(tagId);
        }
        //用于批量删除关系的list
        List<ProductTagRelation> toDeleteList = new ArrayList<>();
        for (ProductTagRelation relation : relationMap.values()) {
            if (relation.getIsDeleted() == 0) {
                relation.setIsDeleted(1);
                toDeleteList.add(relation);
            }
        }
        if (!toDeleteList.isEmpty()) {
            //批量删除关系
            productTagRelationService.updateBatch(toDeleteList);
        }

        if (!toInsertList.isEmpty()) {
            //批量插入
            productTagRelationService.insertBatch(toInsertList);
        }
        if (!toUpdateList.isEmpty()) {
            //批量修改
            productTagRelationService.updateBatch(toUpdateList);
        }

        return product.getId();
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