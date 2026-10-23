package com.beat.mall.module.product.mapper;

import com.beat.mall.module.product.entity.ProductTagRelation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductTagRelationMapper {

    @Select("SELECT * FROM product_tag_relation WHERE id = #{id} AND is_deleted = 0")
    ProductTagRelation getById(@Param("id") Long id);

    @Select("SELECT * FROM product_tag_relation WHERE id = #{id}")
    ProductTagRelation extractById(@Param("id") Long id);

    int update(ProductTagRelation entity);

    int insert(ProductTagRelation entity);

    @Update("UPDATE product_tag_relation SET is_deleted = 1, update_time = #{time} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM product_tag_relation WHERE is_deleted = 0")
    List<ProductTagRelation> getAll();

    //批量新增
    void insertBatch(@Param("list") List<ProductTagRelation> toInsertList);

    //批量修改
    void updateBatch(@Param("list") List<ProductTagRelation> toUpdateList);

    //根据产品id查询关系(包括被删除的)
    @Select("select * from product_tag_relation where product_id = #{productId}")
    List<ProductTagRelation> extractByProductId(@Param("productId") Long productId);

    //根据产品id查找关系（未删除的）
    @Select("select * from product_tag_relation where product_id = #{productId} and is_deleted = 0")
    List<ProductTagRelation> getByProductId(Long productId);

    //根据标签id查询关系
    @Select("select * from product_tag_relation where tag_id = #{tagId} and is_deleted = 0")
    List<ProductTagRelation> getByTagId(@Param("tagId") Long tagId);

    //根据标签id批量查询关系
    List<ProductTagRelation> getByTagIds(@Param("tagIds") List<Long> tagIds);
}