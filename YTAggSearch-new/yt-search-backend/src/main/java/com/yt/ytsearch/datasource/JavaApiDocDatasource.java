package com.yt.ytsearch.datasource;

import com.yt.common.interfaceinfo.JavaApiDocSearch;
import com.yt.common.model.vo.DocInfoVO;
import com.yt.ytsearch.common.ErrorCode;
import com.yt.ytsearch.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

@Service
public class JavaApiDocDatasource implements Datasource<DocInfoVO> {

    @DubboReference
    private JavaApiDocSearch javaApiDocSearch;


    @Override
    public List<DocInfoVO> doSearch(String searchTest, long pageNum, long pageSize) {
        if (StringUtils.isBlank(searchTest)) {
            return new ArrayList<>();
        }
        if (pageNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DocInfoVO> docInfoVOS = javaApiDocSearch.searchJavaApiDocSearch(searchTest, pageNum - 1, Long.valueOf(pageSize).intValue());
        return docInfoVOS;
    }
}
