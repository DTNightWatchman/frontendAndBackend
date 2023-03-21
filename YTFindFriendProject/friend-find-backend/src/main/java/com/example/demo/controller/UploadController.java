package com.example.demo.controller;

import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtil;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import com.example.demo.utils.PictureUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("/upload")
@RestController
public class UploadController {

    @Resource
    private UserService userService;

    @PostMapping("/avatar")
    public BaseResponse<String> uploadAvatar(@RequestParam(required = false ,name = "avatar.file")MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String res = userService.updateUserAvatar(file, request);
        return ResultUtil.success(res);
    }

}
