package com.beat.mall.console.controller.product;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.product.ProductInfo;
import com.beat.mall.console.domain.product.ProductListVo;
import com.beat.mall.console.domain.product.ProductVo;
import com.beat.mall.console.request.ProductDto;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.entity.ProductTagRelation;
import com.beat.mall.module.product.entity.Tag;
import com.beat.mall.module.product.service.BaseProductService;
import com.beat.mall.module.product.service.ProductService;
import com.beat.mall.module.product.service.ProductTagRelationService;
import com.beat.mall.module.product.service.TagService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductTagRelationService productTagRelationService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BaseProductService baseProductService;

    //产品分页模糊查询
    @RequestMapping("/product/list")
    @ResponseBody
    public Response productList(@VerifiedUser User loginUser, @RequestParam(name = "page") Integer page,
                                @RequestParam(name = "productName", required = false) String productName,
                                @RequestParam(name = "categoryName", required = false) String categoryName,
                                @RequestParam(name = "tagName", required = false) String tagName) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        String pageSize = SpringUtil.getProperty("application.pagesize");
        Map<String, List<Long>> resolveSearchIds = baseProductService.resolveSearchIds(categoryName, tagName);
        if (resolveSearchIds == null) {
            //说明找不到与之匹配的类目名和标签名，返回空
            ProductListVo productListVo = new ProductListVo().setList(new ArrayList<>())
                    .setTotal(0)
                    .setPageSize(Integer.parseInt(pageSize));
            return new Response(1001, productListVo);
        }

        List<Long> categoryIds = resolveSearchIds.get("categoryIds");
        List<Long> productIds = resolveSearchIds.get("productIds");
        productName = productName != null ? productName.trim() : null;

        //查总数
        Integer total = productService.countForConsole(productName, categoryIds, productIds);
        //查列表
        List<Product> productList = new ArrayList<>();
        if (total > 0) {
            productList = productService.pageForConsole(page, Integer.parseInt(pageSize), productName, categoryIds, productIds);
        }

        //组装vo
        List<ProductInfo> productInfoList = new ArrayList<>();
        productList.stream().forEach(product -> {
            productInfoList.add(new ProductInfo().setImage(
                            product.getImages().split("\\$")[0])
                    .setName(product.getName())
                    .setPrice(product.getPrice())
                    .setProductId(product.getId()));
        });
        ProductListVo productListVo = new ProductListVo();
        productListVo.setList(productInfoList);
        productListVo.setTotal(total);
        productListVo.setPageSize(Integer.valueOf(pageSize));
        return new Response(1001, productListVo);
    }

    //产品详情
    @RequestMapping("/product/info")
    @ResponseBody
    public Response productInfo(@VerifiedUser User loginUser, @RequestParam(name = "productId") Long productId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Product product = productService.getById(productId);
        if (BaseUtil.isEmpty(product)) {
            log.error("Can't find product id:{}", productId);
            return new Response(3002);
        }
        //根据产品id查找关系（未删除的）
        List<ProductTagRelation> relationList = productTagRelationService.getByProductId(productId);
        if (BaseUtil.isEmpty(relationList)) {
            log.error("No tags found for this product id:{}", productId);
            return new Response(3010);
        }
        List<Long> tagIdsList = relationList.stream()
                .map(ProductTagRelation::getTagId)
                .toList();
        //批量查询tag
        List<Tag> tagList = tagService.getByIds(tagIdsList);
        if (BaseUtil.isEmpty(tagList)) {
            log.error("No tags found for this product id:{}", productId);
            return new Response(3010);
        }
        //将标签名收集成一个集合
        List<String> tags = tagList.stream()
                .map(Tag::getName)
                .toList();

        ProductVo productVo = new ProductVo();
        productVo.setImages(Arrays.stream(product.getImages().split("\\$")).toList());
        productVo.setTags(tags);
        productVo.setName(product.getName());
        productVo.setSaleAmount(Integer.valueOf(0));// 假设销售量为0
        productVo.setPrice(product.getPrice());
        productVo.setSummary(product.getSummary());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sdf.format(product.getCreateTime() * 1000L);
        String updateTime = sdf.format(product.getUpdateTime() * 1000L);
        productVo.setCreateTime(createTime);
        productVo.setUpdateTime(updateTime);
        return new Response(1001, productVo);
    }

    //新增产品
    @PostMapping("/product/create")
    public Response productCreate(@VerifiedUser User loginUser, @RequestBody ProductDto productDto) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        if (BaseUtil.isEmpty(productDto)) {
            return new Response(3051);
        }
        Long categoryId = productDto.getCategoryId();
        String images = productDto.getImages();
        String name = productDto.getName().trim();
        Float price = productDto.getPrice().floatValue();
        String summary = productDto.getSummary().trim();
        String tags = productDto.getTags();//"A$B$C$D"（注意，使⽤$拼接）
        try {
            baseProductService.edit(null, categoryId, images, name, price, summary, tags);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Create product fail.", e);
            return new Response(3006);
        }
    }

    //修改产品
    @RequestMapping("/product/update")
    public Response productUpdate(@VerifiedUser User loginUser,
                                  @RequestParam(name = "productId") Long productId,
                                  @RequestParam(name = "categoryId") Long categoryId,
                                  @RequestParam(name = "images") String images,
                                  @RequestParam(name = "name") String name,
                                  @RequestParam(name = "price") Float price,
                                  @RequestParam(name = "summary") String summary,
                                  @RequestParam(name = "tags") String tags) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        name = name.trim();
        if (BaseUtil.isEmpty(name)) {
            return new Response(3051);
        }
        try {
            baseProductService.edit(productId, categoryId, images, name, price, summary, tags);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Update product fail.", e);
            return new Response(3007);
        }
    }

    //逻辑删除产品
    @RequestMapping("/product/delete")
    public Response productDelete(@VerifiedUser User loginUser, @RequestParam(name = "productId") Long productId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        try {
            productService.delete(productId);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Delete product fail.", e);
            return new Response(3008);
        }
    }

}
