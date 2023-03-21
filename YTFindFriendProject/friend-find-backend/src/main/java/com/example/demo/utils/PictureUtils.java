package com.example.demo.utils;

import com.example.demo.common.ErrorCode;
import com.example.demo.configuration.MyPicConfig;
import com.example.demo.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.utils.SpringBeanUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Slf4j
public class PictureUtils {


    public static String uploadMultipartFile(MultipartFile file) {
        MyPicConfig myPicConfig = SpringBeanUtils.getBean("myPicConfig");
        if (file == null ) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String fileName = UUID.randomUUID().toString();
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String finalName = fileName + fileType;
        try {
            file.transferTo(new File(myPicConfig.getFilePath() + finalName));

        } catch (IOException e) {
            log.error("保存图片失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR ,"保存图片失败");
        }
        return myPicConfig.getDomain() + myPicConfig.getImagePath() + finalName;
    }
}
