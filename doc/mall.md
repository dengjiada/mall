# 商城APP

baseUrl: http://localhost:8080

### 1、产品列表

/product/list

参数：

page:[Int]

keyword:[String],(required=false)

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{
    list:[
      {
        image:{url:[String],ar:[Float]},
        name:[String],
        price:[Float],
        categoryName:[String],
        productId:[Int]
      }
    ],
    isEnd:[Boolean]
  } 
}
```

### 2、产品详情

/product/info

参数：

productId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    images:[String[]],
    tags:[String[]],
    name:[String], 
    saleAmount:[Int],
    price:[Float], 
    summary:[String],
    categoryName:[String],
    categoryImage:[String]
  }     
}
```

### 3、类目列表

/category/list

参数：

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{
    list:[
      {
        name:[String],
        image:[String],
        categoryId:[Int]
        children:[Int[]]
      }
    ]
  }  
}
```

------

# 商城CONSOLE

baseUrl: http://localhost:8081

### 1、产品列表

/product/list

参数：

page:[Int]

keyword:[String],(required=false)

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    list:[
      {
        image:[String],
        name:[String],
        price:[Float],
        productId:[Int]
      }
    ],
    total:[Int],
    pageSize:[Int]
  }  
}
```

### 2、产品详情

/product/info

参数：

productId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    images:[String[]],
    tags:[String[]],
    name:[String], 
    saleAmount:[Int],
    price:[Float], 
    summary:[String],
    createTime:[String],
    updateTime:[String]
  }   
}
```

### 3、产品新增

/product/create

参数：

categoryId:[Int]

images:[String]

name:[String]

price:[Float]

summary:[String]

tags:[String]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 4、产品修改

/product/update

参数：

productId:[Int]

categoryId:[Int]

images:[String]

name:[String]

price:[Float]

summary:[String]

tags:[String]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 5、产品删除

/product/delete

参数：

productId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 6、类目列表

/category/list

参数：

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    list:[
      {
        name:[String],
        image:[String],
        categoryId:[Int],
        children:[Int[]]
      }
    ]
  }  
}
```

### 7、类目详情

/category/info

参数：

categoryId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    name:[String],
    image:[String],
    children:[Int[]],
    createTime:[String],
    updateTime:[String]
  }  
}
```

### 8、类目新增

/category/create

参数：

name:[String]

image:[String]

parentId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 9、类目修改

/category/update

参数：

categoryId:[Int]

parentId:[Int]

name:[String]

image:[String]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 10、类目删除

/category/delete

参数：

categoryId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 11、标签列表

/tag/list

参数：

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    list:[
      {
        name:[String],
        tagId:[Int]
      }
    ]
  }  
}
```

### 12、标签详情

/tag/info

参数：

tagId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  },
  result:{  
    name:[String],
    createTime:[String],
    updateTime:[String]
  }  
}
```

### 13、标签新增

/tag/create

参数：

name:[String]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 14、标签修改

/tag/update

参数：

tagId:[Int]

name:[String]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

### 15、标签删除

/tag/delete

参数：

tagId:[Int]

返回：

```json
{
  status:{
    code:[Int],
    msg:[String]
  }
}
```

------

# 数据库设计

### 产品表设计

```sql
CREATE TABLE `product` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `images` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图，$拼接',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `summary` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '概要',
  `create_time` int unsigned NOT NULL,
  `update_time` int unsigned NOT NULL,
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

ALTER TABLE `product` ADD COLUMN `category_id` bigint unsigned AFTER `id`;
```

### 类目表设计

```sql
CREATE TABLE `category`
(
    `id`          bigint unsigned                                               NOT NULL AUTO_INCREMENT,
    `parent_id`   bigint unsigned                                               not null comment '父ID：0-表示为顶级类目 其他-表示为子类目',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '名称',
    `image`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片',
    `create_time` int unsigned                                                  NOT NULL,
    `update_time` int unsigned                                                  NOT NULL,
    `is_deleted`  tinyint unsigned                                              NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    key `idx_parent` (`parent_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='类目表';
```

### file表设计

```sql
CREATE TABLE `file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint unsigned NOT NULL COMMENT '类型：1-图片 2-视频 3-文件',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'url',
  `create_time` int unsigned NOT NULL,
  `update_time` int unsigned NOT NULL,
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='file表';
```

### 用户表设计

```sql
CREATE TABLE `user` (
  `id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `country_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '86',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户手机号码',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册邮箱号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户头像url',
  `personal_profile` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户背景封面',
  `gender` tinyint UNSIGNED NOT NULL COMMENT '性别，1-男，2-女',
  `birthday` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生日 1999-01-01',
  `wechat_open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信',
  `wechat_union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信号',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `register_time` int UNSIGNED NOT NULL COMMENT '记录用户加入时间',
  `register_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册ip',
  `last_login_time` int UNSIGNED NOT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录ip',
  `is_ban` tinyint UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否禁用1-是0-否',
  `extra` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'json',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间',
  `update_time` int UNSIGNED NOT NULL COMMENT '修改时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone_country_code` (`phone`,`country_code`),
  KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 明文密码：root123456
-- 密码加密串：ee0076c8a462e8e1f0e844044d8fb94f
INSERT INTO `user` (`id`, `country_code`, `phone`, `email`, `username`, `password`, `avatar`, `personal_profile`, `cover_image`, `gender`, `birthday`, `wechat_open_id`, `wechat_union_id`, `wechat_no`, `country`, `province`, `city`, `register_time`, `register_ip`, `last_login_time`, `last_login_ip`, `is_ban`, `extra`, `create_time`, `update_time`, `is_deleted`) VALUES
                                                                                                                                                                                                                                                                                                                                                                               (1, '86', '18717759159', NULL, 'admin', 'ee0076c8a462e8e1f0e844044d8fb94f', 'https://nftcn.oss-cn-shanghai.aliyuncs.com/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1671035411, '0:0:0:0:0:0:0:1', 1671035411, '0:0:0:0:0:0:0:1', 0, NULL, 1671035411, 1671035411, 0),
                                                                                                                                                                                                                                                                                                                                                                               (2, '86', '18185895384', NULL, 'admin01', 'ee0076c8a462e8e1f0e844044d8fb94f', 'https://nftcn.oss-cn-shanghai.aliyuncs.com/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1671096992, '0:0:0:0:0:0:0:1', 1671110744, '192.168.0.21', 0, NULL, 1671096992, 1671110744, 0);

ALTER TABLE `user` MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id', AUTO_INCREMENT=3;
```

### tag表

```sql
create table `tag`
(
    `id`          bigint unsigned                                              not null auto_increment comment '标签ID',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null comment '标签名称',
    `create_time` int unsigned                                                 not null comment '创建时间',
    `update_time` int unsigned                                                 not null comment '修改时间',
    `is_deleted`  tinyint unsigned                                             not null default '0' comment '是否被逻辑删除：0-未删除 1-已删除',
    primary key (`id`),
    unique key `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='tag表';
```

### product_tag_relation表

```sql
create table `product_tag_relation`
(
    `id`          bigint unsigned  not null auto_increment comment 'ID',
    `product_id`  bigint unsigned  not null comment '产品ID',
    `tag_id`      bigint unsigned  not null comment '标签ID',
    `create_time` int unsigned     not null comment '创建时间',
    `update_time` int unsigned     not null comment '修改时间',
    `is_deleted`  tinyint unsigned not null default '0' comment '是否被逻辑删除：0-未删除 1-已删除',
    primary key (`id`),
    unique key `uk_product_tag` (`product_id`, `tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='product_tag_relation表';
```
