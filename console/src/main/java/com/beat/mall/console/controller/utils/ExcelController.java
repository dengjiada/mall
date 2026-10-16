package com.beat.mall.console.controller.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.beat.mall.module.misc.dto.ProductExcelDto;
import com.beat.mall.module.product.entity.Product;
import com.beat.mall.module.product.service.ProductService;
import com.beat.mall.utils.BeanCopyUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ExcelController {

    @Autowired
    private ProductService productService;

    @GetMapping("/excel/export")
    public void exportProducts(HttpServletResponse response) throws IOException {
        List<Product> productList = productService.getAll();
        List<ProductExcelDto> dto = BeanCopyUtil.copyList(productList, ProductExcelDto.class);
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("产品信息", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ProductExcelDto.class)
                .sheet("产品数据")
                .doWrite(dto);
    }

    @PostMapping("/excel/import")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        String ret = null;
        List<ProductExcelDto> list = null;
        try {
            list = EasyExcel.read(file.getInputStream())
                    .head(ProductExcelDto.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error("读取 Excel 文件失败", e.getMessage());
            ret = "fail";
            return ret;
        }
        if (null == list || list.isEmpty()) {
            log.error("导入数据为空，请检查 Excel 文件内容");
            ret = "fail";
            return ret;
        }
        int now = (int) (System.currentTimeMillis() / 1000);
        List<Product> products = list.stream()
                .map(dto -> {
                    Product product = BeanCopyUtil.copy(dto, Product.class);
                    product.setCreateTime(now);// 手动填充字段
                    product.setUpdateTime(now);// 手动填充字段
                    if (product.getIsDeleted() == null) {
                        product.setIsDeleted(0);
                    }
                    return product;
                })
                .collect(Collectors.toList());
        productService.insertBatch(products);
        ret = "success";
        return ret;
    }

    @GetMapping("/excel/zip/export")
    public void exportZip(HttpServletResponse response) throws Exception {
        List<Product> all = productService.getAll();
        log.info("开始查询，商品{}", all.get(0));
        // 产品分成 10 组：id % 10
        Map<Long, List<Product>> groupMap = all.stream()
                .filter(p -> p != null && p.getId() != null)
                .collect(Collectors.groupingBy(p -> p.getId() % 10));
        log.info("分组完成，共有 {} 个分组", groupMap.size());
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("product_export_");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        log.info("创建临时目录：{}", tempDir);
        // 提交任务
        List<Future<File>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Long idx = (long) i;
            List<Product> subList = groupMap.getOrDefault(idx, Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (subList.isEmpty()) continue;
            futures.add(pool.submit(() -> {
                try {
                    String filePath = tempDir.resolve("product_" + idx + ".xlsx").toString();
                    List<ProductExcelDto> dtoList = BeanCopyUtil.copyList(subList, ProductExcelDto.class);
                    EasyExcel.write(filePath, ProductExcelDto.class).sheet("产品数据").doWrite(dtoList);
                    return new File(filePath);
                } catch (Exception e) {
                    log.error("线程写 Excel 出错, idx=" + idx);
                    throw e;
                }
            }));
        }
        log.info("等待所有任务完成");
        // 等待任务完成
        for (Future<File> f : futures) f.get();
        pool.shutdown();
        log.info("所有任务完成");
        // 打包 zip
        File sourceDir = tempDir.toFile();
        File zipFile = new File(tempDir.getParent().toFile(), "products.zip");
        log.info("开始打包，临时目录：{}，压缩文件：{}", sourceDir, zipFile);
        // 使用 zip(String srcPath, String zipPath)
        ZipUtil.zip(sourceDir.getAbsolutePath(), zipFile.getAbsolutePath());
        // 下载
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=products.zip");
        try (OutputStream out = response.getOutputStream();
             InputStream in = Files.newInputStream(zipFile.toPath())) {
            IoUtil.copy(in, out);
        }
        // 清理临时文件
        cn.hutool.core.io.FileUtil.del(tempDir);
    }

    @PostMapping("/excel/zip/import")
    public String importZip(@RequestParam("file") MultipartFile file) throws Exception {
        String ret = null;
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("product_import_");
        File zip = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename())).toFile();
        file.transferTo(zip);
        // 解压 zip
        ZipUtil.unzip(zip, tempDir.toFile());
        // 查找所有 xlsx 文件
        File[] excels = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (null == excels || excels.length == 0) {
            log.error("Zip 文件中未找到任何 Excel 文件");
            ret = "fail";
            return ret;
        }
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(10, excels.length));
        List<Future<?>> futures = new ArrayList<>();
        for (File excel : excels) {
            futures.add(pool.submit(() -> {
                try {
                    List<ProductExcelDto> dtoList = EasyExcel.read(excel)
                            .head(ProductExcelDto.class)
                            .sheet()
                            .doReadSync();
                    List<Product> products = dtoList.stream()
                            .map(dto -> {
                                Product p = BeanCopyUtil.copy(dto, Product.class);
                                int now = (int) (System.currentTimeMillis() / 1000);
                                p.setCreateTime(now);
                                p.setUpdateTime(now);
                                if (p.getIsDeleted() == null) {
                                    p.setIsDeleted(0);
                                }
                                return p;
                            })
                            .collect(Collectors.toList());
                    // 分批插入
                    int batchSize = 1000;
                    for (int i = 0; i < products.size(); i += batchSize) {
                        int end = Math.min(i + batchSize, products.size());
                        List<Product> batch = products.subList(i, end);
                        productService.insertBatch(batch);
                    }
                } catch (Exception e) {
                    log.error("导入文件 {} 失败: {}", excel.getName(), e.getMessage());
                }
            }));
        }
        // 等待全部完成
        for (Future<?> f : futures) f.get();
        pool.shutdown();
        cn.hutool.core.io.FileUtil.del(tempDir);
        ret = "success";
        return ret;
    }
}
