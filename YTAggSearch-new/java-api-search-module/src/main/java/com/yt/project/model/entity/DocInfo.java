package com.yt.project.model.entity;

import lombok.Data;

@Data
public class DocInfo {

    /**
     * 文章id
     */
    private int id;

    /**
     * 标题
     */
    private String title;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 内容
     */
    private String content;
}
