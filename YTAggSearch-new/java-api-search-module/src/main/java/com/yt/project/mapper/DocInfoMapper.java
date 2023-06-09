package com.yt.project.mapper;

import com.yt.project.model.entity.DocInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lenovo
* @description 针对表【doc_info(存储Java的api文档信息)】的数据库操作Mapper
* @createDate 2023-05-29 17:15:09
* @Entity com.yt.project.model.entity.DocInfo
*/
@Mapper
public interface DocInfoMapper extends BaseMapper<DocInfo> {

    /**
     * 批量插入
     * @param entities
     * @return 插入的行数
     */
    Integer batchInsert(List<DocInfo> entities);

}




