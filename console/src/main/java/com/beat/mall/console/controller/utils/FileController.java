package com.beat.mall.console.controller.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class FileController {

    @Autowired
    private FileUtil fileUtil;

    @PostMapping("/upload/file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        try {
            fileName = fileUtil.saveFile(file, "file");
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != fileName ? "上传成功：" + fileName : "上传失败.";
    }

    @PostMapping("/upload/video")
    public String uploadVideo(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        try {
            return fileUtil.saveFile(file, "video");
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != fileName ? "上传成功：" + fileName : "上传失败.";
    }

    @PostMapping("/upload/image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        try {
            return fileUtil.saveFile(file, "image");
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != fileName ? "上传成功：" + fileName : "上传失败.";
    }
}
