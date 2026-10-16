package com.beat.mall.module.product.mapper;

import com.beat.mall.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("select * from product where id = #{id} and is_deleted = 0")
    Product getById(@Param("id") Long id);

    @Select("select * from product where id = #{id}")
    Product extractById(@Param("id") Long id);

    int update(@Param("product") Product product);

    int insert(@Param("product") Product product);

    @Update("update product set is_deleted=1, update_time=#{time} where id=#{id} limit 1")
    int delete(@Param("id") Long id, @Param("time") Integer time);

    @Select("select * from product where is_deleted = 0")
    List<Product> getAll();

    List<Product> selectPageForConsole(@Param("offset") int begin, @Param("pageSize") int pageSize,
                                       @Param("keyword") String keyword);

    Integer selectCountForConsole(@Param("keyword") String keyword);

    List<Product> selectPageForApp(@Param("offset") int begin, @Param("pageSize") int pageSize,
                                   @Param("keyword") String keyword, @Param("categoryIds") String categoryIds);

    @Update("update product set is_deleted=1, update_time=#{time} where category_id=#{categoryId}")
    int deleteByCategoryId(@Param("categoryId") Long categoryId, @Param("time") Integer time);

    void insertBatch(@Param("list") List<Product> products);

}
