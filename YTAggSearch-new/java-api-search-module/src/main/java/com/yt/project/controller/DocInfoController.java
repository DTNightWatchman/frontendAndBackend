package com.yt.project.controller;

import com.yt.project.common.BaseResponse;
import com.yt.project.common.ResultUtils;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.service.DocInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocInfoController {

    @Resource
    private DocInfoService docInfoService;

    @GetMapping("/all")
    public BaseResponse<List<DocInfo>> getAll() {
        return ResultUtils.success(docInfoService.list());
    }
}
