package com.yt.ytsearch.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface Datasource<T> {

    List<T> doSearch(String searchTest, long pageNum, long pageSize);
}
