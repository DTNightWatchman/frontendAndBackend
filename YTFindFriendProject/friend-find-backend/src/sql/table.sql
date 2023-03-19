create table if not exists user
(
	username varchar(256) null comment '用户昵称',
	id bigint auto_increment comment 'id'
		primary key,
	userAccount varchar(256) null comment '账号',
	avatarUrl varchar(256) null comment '用户头像',
	gender tinyint null comment '性别',
	password varchar(512) not null comment '密码',
	phone varchar(128) null comment '电话',
	email varchar(512) null comment '邮箱',
	userStatus int default 0 not null comment '0是正常',
	createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
	updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
	isDelete tinyint default 0 not null comment '是否删除',
	userRole int default 0 not null comment '0是普通用户，1是管理员',
	planetCode varchar(512) null comment '编号',
	tags varchar(1024) null comment '标签列表'
)
comment '用户';