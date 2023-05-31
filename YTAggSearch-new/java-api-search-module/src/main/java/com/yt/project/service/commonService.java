package com.yt.project.service;

import com.yt.common.interfaceinfo.JavaApiDocSearch;
import com.yt.common.model.vo.DocInfoVO;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class commonService implements JavaApiDocSearch {

    @Resource
    private DocInfoService docInfoService;

    @Override
    public List<DocInfoVO> searchJavaApiDocSearch(String searchTest, Long current, Integer size) {
        List<DocInfoVO> docInfoVOS = docInfoService.searchDocInfoFromRedis(searchTest, current, size);
        System.err.println(docInfoVOS);
        return docInfoVOS;
    }
}
