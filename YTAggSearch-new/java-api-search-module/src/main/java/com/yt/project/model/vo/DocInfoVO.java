package com.yt.project.model.vo;

import lombok.Data;

@Data
public class DocInfoVO {

    private String title;

    private String url;

    /**
     * 正文的一段摘要
     */
    private String desc;
}
