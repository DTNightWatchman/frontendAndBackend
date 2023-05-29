package com.yt.project.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Weight implements Serializable {

    /**
     * docId
     */
    private int docId;

    /**
     * 权重
     */
    private int weight;
}
