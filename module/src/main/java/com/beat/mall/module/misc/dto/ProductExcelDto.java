package com.beat.mall.module.misc.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProductExcelDto {

    @ExcelProperty("分类ID")
    private Long categoryId;

    @ExcelProperty("图片")
    private String images;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("价格")
    private float price;

    @ExcelProperty("概要")
    private String summary;

    @ExcelProperty("创建时间")
    private Integer createTime;

    @ExcelProperty("更新时间")
    private Integer updateTime;

    @ExcelProperty("是否删除")
    private Integer isDeleted;
}
