package com.yt.ytsearch.controller;

import com.yt.ytsearch.common.BaseResponse;
import com.yt.ytsearch.common.ErrorCode;
import com.yt.ytsearch.common.ResultUtils;
import com.yt.ytsearch.datasource.Datasource;
import com.yt.ytsearch.datasource.DatasourceRegister;
import com.yt.ytsearch.exception.BusinessException;
import com.yt.ytsearch.model.dto.search.SearchRequest;
import com.yt.ytsearch.model.enums.SearchTypeEnum;
import org.elasticsearch.action.search.SearchType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/aggregation")
public class SearchAllController {

    @Resource
    private DatasourceRegister datasourceRegister;

    @PostMapping("/search")
    public BaseResponse<List<Object>> searchAll(@RequestBody SearchRequest searchRequest) {
        // 通过tab获取到搜索信息
        List<String> types = SearchTypeEnum.getValues();
        if (!types.contains(searchRequest.getTab())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Datasource datasource  = datasourceRegister.getDatasourceByType(searchRequest.getTab());
        // System.out.println(datasource);
        List<Object> res = datasource.doSearch(searchRequest.getSearchTest(), searchRequest.getCurrent(), searchRequest.getPageSize());
        return ResultUtils.success(res);
    }

}
