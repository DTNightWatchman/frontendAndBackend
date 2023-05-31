package com.yt.project.service;

import com.yt.common.model.vo.DocInfoVO;
import com.yt.project.model.entity.DocInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author lenovo
* @description 针对表【doc_info(存储Java的api文档信息)】的数据库操作Service
* @createDate 2023-05-29 17:15:09
*/
public interface DocInfoService extends IService<DocInfo> {

    /**
     * 根据搜索句进行搜索
     * @param line
     * @param current
     * @param size
     */
    List<DocInfoVO> searchDocInfo(String line, long current, int size);

    /**
     * 从redis中进行chax
     * @param line
     * @param current
     * @param size
     * @return
     */
    List<DocInfoVO> searchDocInfoFromRedis(String line, long current, int size);

}
