package com.beat.mall.module.misc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("file")
public class File {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    private Integer type;
    private String url;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;

}