# 重构的搜索项目

## 2023-5-12

1. 前端框架搭建

## 2023-5-13

1. 前端框架的pro-component升级至1.1.25版本
2. 后端框架搭建

## 2023-5-14

1. 前端主页面展示

2. 引入一个hook使得页面选择同步到url上，保证刷新页面保持页面原有状态

   > npm i @ahooksjs/use-url-state -S

todo

后端post，picture，user接口开发

## 2023-5-18

开发picture接口，使用门面模块

后续开发计划：

尽量使用技术：redis缓存，Dubbo框架



## 2023-5-24

1. 开发Java api文档搜索部分：
   1. 通过递归循环遍历所有文件

2. 开发搜索

## 2023-5-24

1. 开发分词搜索
2. 去掉暂停词
3. 根据优先队列汇总结果
4. 返回结果
5. 功能测试

- [x] api文档搜索部分开发，提交代码

### 引入dubbo

> [Dubbo 入门 | Apache Dubbo](https://cn.dubbo.apache.org/zh-cn/overview/quickstart/)

拉取测试代码：

> git clone --depth=1 --branch master git@github.com:apache/dubbo-samples.git

spring boot引入：

> [3 - Dubbo Spring Boot Starter 开发微服务应用 | Apache Dubbo](https://cn.dubbo.apache.org/zh-cn/overview/quickstart/java/spring-boot/)

使用dubbo提供的一个简单的zookeeper来作为注册中心

注意报错：如果出现提供者或者消费者无法连接zookeeper的时候，将zookeeper 和服务提供者的jdk版本设置为 jdk1.8

- [x] 示例项目测试成功

Dubbo的底层是Triple协议，不是http协议



模块作为服务提供者，提供搜索的函数

todo:

zookeeper连接不上，需要后续研究，如果不行，可能需要换成nacos

## 2023-5-26

调通dubbo和zookeeper

总结：

1. 引入依赖，需要注意的是依赖的xml，需要将log4j的冲突取消，否则回出现报错（log4j经常出现版本冲突问题，后续开发需要注意）

```
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-samples-spring-boot-interface</artifactId>
    <version>${project.parent.version}</version>
</dependency>

<!-- dubbo -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-dependencies-zookeeper-curator5</artifactId>
    <type>pom</type>
    <exclusions>
        <exclusion>
            <artifactId>slf4j-reload4j</artifactId>
            <groupId>org.slf4j</groupId>
        </exclusion>
    </exclusions>
</dependency>
```



2. 启动zookeeper注册中心，可以使用本地的zookeeper，也可以使用自主搭建的zookeeper，这里我使用了单个zookeeper，没有搭建集群
3. 用来注册的公共接口需要注意，要不单独抽象出一个包出来，如果不抽象，那么从com开始的路径是需要一致的，否则无法从zookeeper中获取

4. 配置：

```
dubbo:
  application:
    name: dubbo-springboot-demo-consumer
    qos-enable: false
  protocol:
    name: dubbo
    port: -1
  registry:
    address: zookeeper://192.168.10.148:2181
```

- [x] 完成dubbo和zookeeper联动
- [ ] 开发接口联动





















