package com.beat.mall.module.product.entity;

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
@TableName("product_tag_relation")
public class ProductTagRelation {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    private Long productId;
    private Long tagId;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;

}