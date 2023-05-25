package com.yt.project.service;

import com.yt.project.model.vo.DocInfoVO;

import java.util.List;

public interface DocInfoService {

    /**
     * 根据搜索句进行搜索
     * @param line
     * @param current
     * @param size
     */
    List<DocInfoVO> searchDocInfo(String line, long current, int size);
}
