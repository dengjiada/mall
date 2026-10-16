package com.beat.mall.module.misc.mapper;

import com.beat.mall.module.misc.entity.File;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FileMapper {

    @Select("SELECT * FROM file WHERE id = #{id} AND is_deleted = 0")
    File getById(@Param("id") Long id);

    @Select("SELECT * FROM file WHERE id = #{id}")
    File extractById(@Param("id") Long id);

    int update(File entity);

    int insert(File entity);

    @Update("UPDATE file SET is_deleted = 1, update_time = #{time} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM file WHERE is_deleted = 0")
    List<File> getAll();

}