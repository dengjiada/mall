package com.beat.mall.app.controller.product;

import com.beat.mall.app.domain.ImageInfo;
import com.beat.mall.app.domain.product.ProductInfo;
import com.beat.mall.app.domain.product.ProductListVo;
import com.beat.mall.app.domain.product.ProductVo;
import com.beat.mall.module.product.entity.Category;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.service.CategoryService;
import com.beat.mall.module.product.service.ProductService;
import com.beat.mall.utils.ImageUtil;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/product/list")
    @ResponseBody
    public Response productList(@RequestParam(name = "page") Integer page,
                                @RequestParam(name = "keyword", required = false) String keyword) {
        String pageSize = SpringUtil.getProperty("application.pagesize");
        List<Product> productList = productService.pageForApp(page, Integer.parseInt(pageSize), keyword);
        Boolean isEnd = productList.size() < Integer.parseInt(pageSize);
        List<ProductInfo> productInfoList = new ArrayList<ProductInfo>();
        List<Category> categories = categoryService.getAll();
        Map<Long, String> categoryMap = categories.stream().
                collect(Collectors.toMap(Category::getId, Category::getName));
        productList.stream().forEach(product -> {
            String imageUrl = Arrays.stream(product.getImages().split("\\$")).toList().getFirst();
            Float ar = ImageUtil.calcAr(imageUrl);
            productInfoList.add(new ProductInfo().setImage(new ImageInfo().setUrl(imageUrl).setAr(ar))
                    .setName(product.getName())
                    .setPrice(product.getPrice())
                    .setCategoryName(categoryMap.get(product.getCategoryId()))
                    .setProductId(product.getId()));
        });
        ProductListVo productListVo = new ProductListVo();
        productListVo.setList(productInfoList);
        productListVo.setIsEnd(isEnd);
        return new Response(1001, productListVo);
    }

    @RequestMapping("/product/info")
    @ResponseBody
    public Response productInfo(@RequestParam(name = "productId") Long productId) {
        Product product = productService.getById(productId);
        ProductVo productVo = new ProductVo();
        if (null != product) {
            Category category = categoryService.getById(product.getCategoryId());
            if (null != category) {
                productVo.setImages(Arrays.stream(product.getImages().split("\\$")).toList());
                productVo.setName(product.getName());
                productVo.setSaleAmount(Integer.valueOf(0));// 假设销售量为0
                productVo.setPrice(product.getPrice());
                productVo.setSummary(product.getSummary());
                productVo.setCategoryName(category.getName());
                productVo.setCategoryImage(category.getImage());
            } else {
                log.error("Category not found(categoryId = " + product.getCategoryId() + ").");
                return new Response(4004);
            }
        } else {
            log.error("Can't find product id:{}", productId);
            return new Response(4004);
        }
        return new Response(1001, productVo);
    }

}
