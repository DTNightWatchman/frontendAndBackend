package com.example.demo.service;

import com.example.demo.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class  AlgorithmUtilsTest {

    @Test
    void test() {
        List<String> tags1 = Arrays.asList("java", "大一", "男");
        List<String> tags2 = Arrays.asList("java", "大一", "女");
        List<String> tags3 = Arrays.asList("python", "大二", "女");


        System.out.println(AlgorithmUtils.minDistance(tags1, tags2));
        System.out.println(AlgorithmUtils.minDistance(tags1, tags3));
    }
}
