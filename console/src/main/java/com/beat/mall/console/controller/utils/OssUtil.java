package com.beat.mall.console.controller.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class OssUtil {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String saveFile(MultipartFile file, FileType type) throws IOException, ClientException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        //获得文件的后缀
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName;
        String datePath = new SimpleDateFormat("yyMM/dd").format(new Date());
        int randomNumber = (int) (Math.random() * 1000) + 1;
        long time = System.currentTimeMillis();
        // 处理图片文件，获取宽高
        if (type == FileType.IMAGE) {
            try (InputStream imgStream = file.getInputStream()) {
                BufferedImage image = ImageIO.read(imgStream);
                if (image == null) {
                    throw new IllegalArgumentException("非法的图片文件");
                }
                int width = image.getWidth();
                int height = image.getHeight();
                fileName = type.getName() + "/" + datePath + "/" + BaseUtil.md5(String.valueOf(randomNumber + time)) + "_" + width + "x" + height + extension;
            }
        } else {
            fileName = type.getName() + "/" + datePath + "/" + BaseUtil.md5(String.valueOf(randomNumber + time)) + extension;
        }
        // 上传到 OSS
        try (InputStream uploadStream = file.getInputStream()) {
            EnvironmentVariableCredentialsProvider credentialsProvider =
                    CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
            try {
                ossClient.putObject(bucketName, fileName, uploadStream);
            } finally {
                ossClient.shutdown();
            }
        }
        // 返回公网 URL
        return "https://" + bucketName + "." + endpoint + "/" + fileName;
    }

}
