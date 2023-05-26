package com.yt.ytsearch.controller;

import com.yt.common.interfaceinfo.JavaApiDocSearch;
import com.yt.common.model.vo.DocInfoVO;
import com.yt.ytsearch.common.BaseResponse;
import com.yt.ytsearch.common.ResultUtils;
import com.yt.ytsearch.model.dto.search.SearchRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/java-api")
public class JavaApiDocController {

    @DubboReference
    private JavaApiDocSearch javaApiDocSearch;

    @PostMapping("search")
    public BaseResponse<List<DocInfoVO>> searchJavaApiDocInfo(@RequestBody SearchRequest searchRequest) {
        List<DocInfoVO> docInfoVOS = javaApiDocSearch.searchJavaApiDocSearch(searchRequest.getSearchTest(), searchRequest.getCurrent(), Long.valueOf(searchRequest.getPageSize()).intValue());
        return ResultUtils.success(docInfoVOS);
    }
    
}
