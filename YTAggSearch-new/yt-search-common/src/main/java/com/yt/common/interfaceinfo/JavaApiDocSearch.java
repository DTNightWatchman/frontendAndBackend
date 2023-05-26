package com.yt.common.interfaceinfo;

import com.yt.common.model.vo.DocInfoVO;

import java.util.List;

public interface JavaApiDocSearch {

    /***
     * 定义 java API搜索部分
     * @param searchTest
     * @param current
     * @param size
     * @return
     */
    List<DocInfoVO> searchJavaApiDocSearch(String searchTest, Long current, Integer size);
}
