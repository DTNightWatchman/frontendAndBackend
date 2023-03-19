package com.example.demo.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入excel数据
 * @author YT
 */
public class ImportExcel {
    public static void main(String[] args) {

        synchronousRead();
    }

    public static void readByListener() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "E:/Javacode/登录管理/20220925/src/main/resources/testExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, TableUserInfo.class, new DemoDataListener()).sheet().doRead();
    }

    public static void synchronousRead() {
        String fileName = "E:/Javacode/登录管理/20220925/src/main/resources/prodExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<TableUserInfo> list = EasyExcel.read(fileName).head(TableUserInfo.class).sheet().doReadSync();
        for (TableUserInfo data : list) {
            System.out.println(data);
        }

    }

}
