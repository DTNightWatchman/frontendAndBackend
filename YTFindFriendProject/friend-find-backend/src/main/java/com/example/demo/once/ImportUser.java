package com.example.demo.once;

import cn.hutool.system.UserInfo;
import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ImportUser {
    public static void main(String[] args) {
        String fileName = "E:\\Javacode\\登录管理\\20220925\\src\\main\\resources\\prodExcel.xlsx";
        List<TableUserInfo> list = EasyExcel.read(fileName).head(TableUserInfo.class).sheet().doReadSync();
        System.out.println("总数：" + list.size());

        Map<String, List<TableUserInfo>> collect = list.stream()
                .filter(tableUserInfo -> StringUtils.isNotEmpty(tableUserInfo.getUsername()))
                .collect(Collectors.groupingBy(TableUserInfo::getUsername));
        System.out.println("不重复数目：" + collect.keySet().size());
    }
}
