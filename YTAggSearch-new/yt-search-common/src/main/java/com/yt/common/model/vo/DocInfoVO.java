package com.yt.common.model.vo;

import java.io.Serializable;

/**
 * 返回结果
 */
public class DocInfoVO implements Serializable {

    private String title;

    private String url;

    /**
     * 正文的一段摘要
     */
    private String desc;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DocInfoVO{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
