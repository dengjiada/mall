package com.beat.mall.console.controller.utils;

import com.beat.mall.module.misc.entity.File;
import com.beat.mall.module.misc.service.FileService;
import com.beat.mall.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class OssController {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private FileService fileService;

    @PostMapping("/oss/upload/file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String url = null;
        try {
            url = ossUtil.saveFile(file, FileType.FILE);
            File entity = new File();
            entity.setType(FileType.FILE.getType());
            entity.setUrl(url);
            fileService.insert(entity);
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != url ? "上传成功：" + url : "上传失败.";
    }

    @PostMapping("/oss/upload/video")
    public String uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = null;
        try {
            url = ossUtil.saveFile(file, FileType.VIDEO);
            File entity = new File();
            entity.setType(FileType.VIDEO.getType());
            entity.setUrl(url);
            fileService.insert(entity);
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != url ? "上传成功：" + url : "上传失败.";
    }

    @PostMapping("/oss/upload/image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        String url = null;
        try {
            url = ossUtil.saveFile(file, FileType.IMAGE);
            File entity = new File();
            entity.setType(FileType.IMAGE.getType());
            entity.setUrl(url);
            fileService.insert(entity);
        } catch (Exception e) {
            log.error("上传失败：" + e.getMessage());
        }
        return null != url ? "上传成功：" + url : "上传失败.";
    }
}
