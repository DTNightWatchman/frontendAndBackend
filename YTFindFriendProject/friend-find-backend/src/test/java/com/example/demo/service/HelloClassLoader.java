package com.example.demo.service;

import java.net.URL;
import java.net.URLClassLoader;

public class HelloClassLoader
{
    public static void main( String[] args ) throws Exception
    {
        URL[] urls = {new URL("http://192.168.1.111:9999/")};
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        Class c = loader.loadClass("Hello");
        c.newInstance();
    }
}