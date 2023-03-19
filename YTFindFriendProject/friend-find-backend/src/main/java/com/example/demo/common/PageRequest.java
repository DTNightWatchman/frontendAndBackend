package com.example.demo.common;

import javassist.SerialVersionUID;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -758178538562344487L;
    /**
     * 页面大小
     */
    private int pageSize = 10;


    /**
     * 起始页面
     */
    private int pageNum = 1;
}
