package com.yt.ytsearch.model.dto.search;

import com.yt.ytsearch.common.PageRequest;
import lombok.Data;

@Data
public class SearchRequest extends PageRequest {

    /**
     * 定义搜索词
     */
    private String searchTest;


    /**
     * 定义类别
     */
    private String tab;

}
