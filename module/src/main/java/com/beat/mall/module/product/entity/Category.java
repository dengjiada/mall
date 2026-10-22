package com.beat.mall.module.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    private Long parentId;
    private String name;
    private String image;
    private Integer createTime;
    private Integer updateTime;
    private Byte isDeleted;

}