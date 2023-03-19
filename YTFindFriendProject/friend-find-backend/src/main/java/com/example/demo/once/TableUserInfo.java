package com.example.demo.once;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TableUserInfo {
    /**
     * id
     */
    @ExcelProperty("成员id")
    private String id;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}
