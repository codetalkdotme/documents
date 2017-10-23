/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/10/23 20:04:21                          */
/*==============================================================*/


drop table if exists fnd_config;

drop table if exists fnd_group_tags;

drop table if exists fnd_lookups;

drop table if exists fnd_notice;

drop table if exists fnd_position_tag_group;

drop table if exists fnd_roles;

drop table if exists fnd_tag_groups;

drop table if exists fnd_tags;

drop table if exists fnd_uploads;

drop table if exists fnd_user_roles;

drop table if exists fnd_user_tags;

drop index uidx_user_login on fnd_users;

drop table if exists fnd_users;

/*==============================================================*/
/* Table: fnd_config                                            */
/*==============================================================*/
create table fnd_config
(
   config_id            int not null auto_increment,
   config_code          varchar(100) not null,
   config_value         varchar(100) not null,
   config_desc          varchar(300) default '0' comment '删除标记 0 未删除 1 已删除',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (config_id)
);

alter table fnd_config comment '系统配置';

/*==============================================================*/
/* Table: fnd_group_tags                                        */
/*==============================================================*/
create table fnd_group_tags
(
   group_tag_id         int not null auto_increment,
   group_id             int not null comment 'tag分组ID',
   tag_id               int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (group_tag_id)
);

alter table fnd_group_tags comment '分组 和 tag映射关系';

/*==============================================================*/
/* Table: fnd_lookups                                           */
/*==============================================================*/
create table fnd_lookups
(
   lookup_id            int not null auto_increment,
   lookup_category      varchar(100) not null,
   lookup_code          varchar(100) not null,
   lookup_value         varchar(300) not null default '0' comment '删除标记 0 未删除 1 已删除',
   lookup_order         int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (lookup_id)
);

alter table fnd_lookups comment 'lookup';

/*==============================================================*/
/* Table: fnd_notice                                            */
/*==============================================================*/
create table fnd_notice
(
   notice_id            bigint not null auto_increment,
   user_id              bigint not null comment '消息接收者用户ID',
   notice_type          int not null comment '通知子类型:
            专干消息
            1：预约挂号，2：咨询，3：预约免疫，4：家庭医生签约申请，5：预约体检，6：入户随访居民回复，7：活动邀请居民回复，8：建档提醒，11：其他
            
            居民消息
            1：预约免疫，2：预约体检，3：入户提醒，11：其他',
   notice_subtype       int comment '通知子类型, 可选',
   from_user_id         bigint comment '消息发起者, 比如预约挂号发起人用户ID',
   notice_content       varchar(300) not null,
   notice_dtl_content   varchar(500),
   is_app               int not null comment '0 否 1 是',
   app_status           int not null default 1 comment 'APP通知状态
            
            1 未读
            2 已读',
   is_push              int not null comment '0 否 1 是',
   push_status          int not null default 1 comment '通知状态
            
            1 未推送
            2 已推送',
   attr1                varchar(200),
   attr2                varchar(200),
   attr3                varchar(200),
   attr4                varchar(200),
   attr5                varchar(200),
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (notice_id)
);

alter table fnd_notice comment '通知基础表';

/*==============================================================*/
/* Table: fnd_position_tag_group                                */
/*==============================================================*/
create table fnd_position_tag_group
(
   position_tg_id       int not null auto_increment,
   position_type        int not null,
   tag_group_id         int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (position_tg_id)
);

alter table fnd_position_tag_group comment '职责 和 tag分组映射关系';

/*==============================================================*/
/* Table: fnd_roles                                             */
/*==============================================================*/
create table fnd_roles
(
   role_id              int not null auto_increment,
   role_code            varchar(50) not null,
   role_name            varchar(100) not null,
   delete_mark          tinyint not null default 0 comment '删除标记 0 未删除 1 已删除',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (role_id)
);

alter table fnd_roles comment '角色';

/*==============================================================*/
/* Table: fnd_tag_groups                                        */
/*==============================================================*/
create table fnd_tag_groups
(
   group_id             int not null auto_increment,
   group_title          varchar(100) not null comment 'tag分组的标题',
   group_desc           varchar(500),
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (group_id)
);

alter table fnd_tag_groups comment 'tag分组';

/*==============================================================*/
/* Table: fnd_tags                                              */
/*==============================================================*/
create table fnd_tags
(
   tag_id               int not null auto_increment,
   tag_text             varchar(100) not null comment '小写, 不允许空格(使用-)',
   tag_hits             bigint not null default 0,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (tag_id)
);

alter table fnd_tags comment '标签，pofo 和 solv公用';

/*==============================================================*/
/* Table: fnd_uploads                                           */
/*==============================================================*/
create table fnd_uploads
(
   upload_id            bigint not null auto_increment,
   user_id              bigint not null comment '上传文件的用户',
   file_url             varchar(200) not null comment '小写, 不允许空格(使用-)',
   file_thumb_url       varchar(200) comment '如果是图片时, 生成缩略图',
   file_original_name   varchar(200) comment '文件名称',
   file_type            varchar(50) comment '文件类型',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (upload_id)
);

alter table fnd_uploads comment '文件上传';

/*==============================================================*/
/* Table: fnd_user_roles                                        */
/*==============================================================*/
create table fnd_user_roles
(
   user_role_id         int not null auto_increment,
   user_id              int not null,
   role_id              int not null,
   role_code            varchar(50) not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (user_role_id)
);

alter table fnd_user_roles comment '用户角色';

/*==============================================================*/
/* Table: fnd_user_tags                                         */
/*==============================================================*/
create table fnd_user_tags
(
   user_tag_id          bigint not null auto_increment,
   user_id              int not null comment '用户ID， 与账号系统uid一一对应',
   tag_id               int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (user_tag_id)
);

alter table fnd_user_tags comment '用户tag表';

/*==============================================================*/
/* Table: fnd_users                                             */
/*==============================================================*/
create table fnd_users
(
   user_id              int not null comment '用户ID， 与账号系统uid一一对应',
   user_login           varchar(200) not null comment '用户登录名, 由英文字母 和 下划线组成',
   user_name            varchar(200) not null comment '用户名称, 允许中文 / 空格等',
   user_profile         varchar(200) comment '用户头像URL, 存放fdfs路径, 示例: "group1/M00/00/03/wKhQA1lbBFaALuwvAAChr-JPsVI9607165"',
   profile_status       int not null comment '头像状态
            1. 待审核
            2. 审核通过
            3. 拒绝',
   position_type        int not null comment '职责类型, 1 开发 2 测试 3 产品 4 设计 5 运维 etc',
   attribute1           varchar(200) comment '扩展字段1',
   attribute2           varchar(200),
   attribute3           varchar(200),
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   unique key AK_uqx_user_id (user_id)
);

alter table fnd_users comment '用户表';

/*==============================================================*/
/* Index: uidx_user_login                                       */
/*==============================================================*/
create unique index uidx_user_login on fnd_users
(
   user_login
);

