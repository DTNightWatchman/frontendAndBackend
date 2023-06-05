-- 创建库
create database if not exists db_java_api_doc;

-- 切换库
use db_java_api_doc;

create table doc_info
(
    id      int auto_increment comment '索引id'
        primary key,
    url     varchar(255) not null comment 'url路径',
    title   varchar(255) null comment '文章标题',
    content mediumtext   null comment '文章内容'
)
    comment '存储Java的api文档信息';
