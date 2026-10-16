package com.beat.mall.console.controller.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileUtil {

    @Value("${upload.image.dir}")
    private String uploadImageDir;

    @Value("${upload.video.dir}")
    private String uploadVideoDir;

    @Value("${upload.file.dir}")
    private String uploadFileDir;

    public String saveFile(MultipartFile file, String type) throws IOException {
        if (null == file || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        int randomNumber = (int) (Math.random() * 1000) + 1;
        long time = System.currentTimeMillis();
        String originalFilename = file.getOriginalFilename();
        //获得文件的后缀
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName;
        // 处理图片文件，获取宽高
        if ("image".equalsIgnoreCase(type)) {
            try (InputStream imgStream = file.getInputStream()) {
                BufferedImage image = ImageIO.read(imgStream);
                if (null == image) {
                    throw new IllegalArgumentException("非法的图片文件");
                }
                int width = image.getWidth();
                int height = image.getHeight();
                fileName = uploadImageDir + randomNumber + time + "_" + width + "x" + height + extension;
            }
        } else if ("video".equalsIgnoreCase(type)) {
            fileName = uploadVideoDir + randomNumber + time + extension;
        } else if ("file".equalsIgnoreCase(type)) {
            fileName = uploadFileDir + randomNumber + time + extension;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        // 上传到本地磁盘目录
        try {
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            log.error("Uplaod file fail.", e);
            return fileName;
        }
    }

}
