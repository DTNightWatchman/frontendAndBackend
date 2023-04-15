package com.yt.searchbackend.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yt.searchbackend.model.entity.Picture;

/**
 * 数据源接口（接入的数据源必须实现）
 * @param <T>
 */
public interface DataSource<T> {


    /**
     * 搜索接口
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
