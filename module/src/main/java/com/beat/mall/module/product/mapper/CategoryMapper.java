package com.beat.mall.module.product.mapper;

import com.beat.mall.module.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE id = #{id} AND is_deleted = 0")
    Category getById(@Param("id") Long id);

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category extractById(@Param("id") Long id);

    int update(Category entity);

    int insert(Category entity);

    @Update("UPDATE category SET is_deleted = 1, update_time = #{time} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM category WHERE is_deleted = 0")
    List<Category> getAll();

    List<Long> getIdsByKeyword(@Param("keyword") String keyword);
}