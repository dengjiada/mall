package com.beat.mall.module.product.mapper;

import com.beat.mall.module.product.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tag WHERE id = #{id} AND is_deleted = 0")
    Tag getById(@Param("id") Long id);

    @Select("SELECT * FROM tag WHERE id = #{id}")
    Tag extractById(@Param("id") Long id);

    int update(Tag entity);

    int insert(Tag entity);

    @Update("UPDATE tag SET is_deleted = 1, update_time = #{time} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM tag WHERE is_deleted = 0")
    List<Tag> getAll();

    //根据标签名批量查询标签（包括被删除的）
    List<Tag> extractByNames(@Param("list") List<String> tagsName);

    //批量查询tag
    List<Tag> getByIds(@Param("list") List<Long> tagIdsList);

    //根据标签名查询标签
    @Select("select * from tag where name like concat('%',#{name},'%')")
    List<Tag> getByNameLike(String name);
}