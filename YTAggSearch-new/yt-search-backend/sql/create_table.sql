# 建表脚本
# @author <a href="https://github.com/DTNightWatchman">YTbaiduren</a>
# @from

-- 创建库
create database if not exists db_search;

-- 切换库
use db_search;
-- auto-generated definition

-- 帖子表
create table post
(
    id         bigint auto_increment comment 'id'
        primary key,
    title      varchar(512)                         null comment '标题',
    content    text                                 null comment '内容',
    tags       varchar(1024)                        null comment '标签列表（json 数组）',
    thumbNum   int      default 0                   not null comment '点赞数',
    favourNum  int      default 0                   not null comment '收藏数',
    userId     bigint                               not null comment '创建用户 id',
    createTime datetime default (CURRENT_TIMESTAMP) not null comment '创建时间',
    updateTime datetime default (CURRENT_TIMESTAMP) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                   not null comment '是否删除'
)
    comment '帖子' collate = utf8mb4_unicode_ci;

create index idx_userId
    on post (userId);


-- 用户信息表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                             not null comment '账号',
    userPassword varchar(512)                             not null comment '密码',
    unionId      varchar(256)                             null comment '微信开放平台id',
    mpOpenId     varchar(256)                             null comment '公众号openId',
    userName     varchar(256)                             null comment '用户昵称',
    userAvatar   varchar(1024)                            null comment '用户头像',
    userProfile  varchar(512)                             null comment '用户简介',
    userRole     varchar(256) default 'user'              not null comment '用户角色：user/admin/ban',
    createTime   datetime     default (CURRENT_TIMESTAMP) not null comment '创建时间',
    updateTime   datetime     default (CURRENT_TIMESTAMP) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                   not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);

