package com.yt.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 存储Java的api文档信息
 * @TableName doc_info
 */
@TableName(value ="doc_info")
@Data
public class DocInfo implements Serializable {
    /**
     * 索引id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * url路径
     */
    private String url;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}