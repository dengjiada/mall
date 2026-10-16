package com.beat.mall.console.controller.product;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.product.ProductInfo;
import com.beat.mall.console.domain.product.ProductListVo;
import com.beat.mall.console.domain.product.ProductVo;
import com.beat.mall.console.request.ProductDto;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.service.ProductService;
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

@Slf4j
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/product/list")
    @ResponseBody
    public Response productList(@VerifiedUser User loginUser, @RequestParam(name = "page") Integer page,
                                @RequestParam(name = "keyword", required = false) String keyword) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        String pageSize = SpringUtil.getProperty("application.pagesize");
        List<Product> productList = productService.pageForConsole(page, Integer.parseInt(pageSize), keyword);
        List<ProductInfo> productInfoList = new ArrayList<ProductInfo>();
        productList.stream().forEach(product -> {
            productInfoList.add(new ProductInfo().setImage(
                            Arrays.stream(product.getImages().split("\\$")).toList().getFirst())
                    .setName(product.getName())
                    .setPrice(product.getPrice())
                    .setProductId(product.getId()));
        });
        ProductListVo productListVo = new ProductListVo();
        productListVo.setList(productInfoList);
        Integer total = productService.countForConsole(keyword);
        productListVo.setTotal(total);
        productListVo.setPageSize(Integer.valueOf(pageSize));
        return new Response(1001, productListVo);
    }

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
            return new Response(4004);
        }
        ProductVo productVo = new ProductVo();
        productVo.setImages(Arrays.stream(product.getImages().split("\\$")).toList());
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
        try {
            productService.edit(null, categoryId, images, name, price, summary);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Create product fail.", e);
            return new Response(4004);
        }
    }

    @RequestMapping("/product/update")
    public Response productUpdate(@VerifiedUser User loginUser,
                                  @RequestParam(name = "productId") Long productId,
                                  @RequestParam(name = "categoryId") Long categoryId,
                                  @RequestParam(name = "images") String images,
                                  @RequestParam(name = "name") String name,
                                  @RequestParam(name = "price") Float price,
                                  @RequestParam(name = "summary") String summary) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        name = name.trim();
        if (BaseUtil.isEmpty(name)) {
            return new Response(3051);
        }
        try {
            productService.edit(productId, categoryId, images, name, price, summary);
            return new Response(1001);
        } catch (Exception e) {
            log.error("Update product fail.", e);
            return new Response(4004);
        }
    }

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
            return new Response(4004);
        }
    }

}
