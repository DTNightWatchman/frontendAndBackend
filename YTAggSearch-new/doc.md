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
- [x] 开发接口联动
  - 开发一个公共的接口，供所有的用户去引用和调用
  - 创建一个公共的maven项目,写入公共接口，在模块中实现接口
  - 因为需要反序列化，所以需要将类型继承序列化接口
  - 跑通过一个模块调用另一个模块流程

修改Java api 文档搜索模块，让backend模块能够调用



#### 细化api 文档搜索功能

将数据存入内存和缓存中，便于之后实现分布式

如何存入数据：

1. 构建docInfo 对应的数据表
   1. 实现方式：创建数据表：id,title,url,content, 以id作为索引字段，进行搜索，便于之后的快速查询
   2. 以redis中的哈希表作为存储结构，方便后续查询
   3. 如何同步数据：
      1. 同步的时候需要对数据库进行加锁，否则会出现同步错误
      2. 倒排索引，直接塞进redis的哈希表中，便于同步数据
      3. 每次查询的结果在redis中缓存30秒，后续的分页可以使用到，如果有查询，就刷新时间
2. 完成项目重构，引入对数据库的操作
3. 出现报错，无法正确引入bean (傻了，忘记修之前的 一个小细节了)，浪费3小时
4. 修改bug，出现content字段存入过长的问题，换成使用mediumtext字段，无长度限制

todo:

- [x] 数据库插入换成使用批量插入，加快解析速度（性能相差不大）

- [ ] 重构搜索部分代码，从redis中和数据库中读取数据

# 2023-5-30

有一个场景，多线程会生产数据，每个线程产生一条数据，就将他插入到数据库中。如何将插入改成批量插入

定义一个批量插入集合，放入数据的时候进行加锁，然后将数据插入：

```
// 添加到批量插入集合
synchronized (batchInsertList) {
    batchInsertList.add(data);

    // 达到阈值时执行批量插入
    if (batchInsertList.size() >= batchSize) {
    	performBatchInsert();
    }
}
```

在线程部分执行完后，需要检查缓冲区内是否还有数据，如果有需要进行插入数据库的操作



想一下：是否需要将项目重构，创建一个getway网关，将流量进行分发，这里同样是使用门面模式，此时就不能使用，不能，使用了dubbo，就不适用了

### 性能优化：

一条一条插入数据：耗时72635ms,73863ms,大概两分多钟

单挑记录插入：79853ms

批量插入后：19483ms，18199ms，18126ms

改为批量插入数据后，将构造索引所化的时间优化了三倍，从一分多钟优化到20s以下

todo:

重构搜索部分:

- [x] 使用查找redis+数据库的方式进行查询
- [x] 将每次的查询词和对应结果缓存
- [x] 模拟分页查询

# 2023-5-31

重构搜索逻辑，将使用批量插入的方式，提升构造正排索引和倒排索引的性能，使得一分多钟的构造时间降低到20秒以下

将每次的搜索结果缓存到redis中 √

同时实现分页查询 √

测试和dubbo连接联动

启动zookeeper

> ./zkServer.sh start

同时启动，es和kibana

修改前端和后端的代码，添加java api 的 menu，添加一个用户Java api的tab，在后端联动这部分模块

todo:

解决远程调用出现 index越界问题？？？？



# 2023-6-1

出现序列化异常问题，大概是因为dubbo默认的序列化是fastjson，不会进行自动的序列化

解决这个bug有两个解决方法：

1. 启动fastjson的自动序列化，据我所知：开了自动化bug有点多。。。
2. 替换序列化器，使用Gson进行序列化

> [Gson | Apache Dubbo](https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/serialization/gson/)

选择方案2 解决序列化问题

替换log：

https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg

删掉：

 https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png

后续开发：

1. 点击加载更多部分代码实现
2. 测试使用canal进行数据同步
3. 测试各种创建线程池的方法，更了解线程池的一些知识。。。！！！ 很重要

开发滚动加载：

怎么将useState的数组中添加元素：

```
setArray([...array, item])
```

需要指定一下迭代方式

完成点击加载更多。

# 2023-6-2

完成前后端部署

