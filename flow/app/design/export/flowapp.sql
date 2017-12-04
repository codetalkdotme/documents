/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/12/4 20:59:34                           */
/*==============================================================*/


drop table if exists fnd_lookup_categories;

drop table if exists fnd_lookup_values;

drop table if exists fnd_notice;

drop table if exists fnd_tag_groups;

drop table if exists fnd_tags;

drop table if exists fnd_user_follow;

drop table if exists fnd_user_settings;

drop table if exists fnd_user_tags;

drop table if exists poll_options;

drop table if exists polls;

drop table if exists post_comments;

drop table if exists posts;

drop table if exists tut_attaches;

drop table if exists tut_comments;

drop table if exists tutorials;

/*==============================================================*/
/* Table: fnd_lookup_categories                                 */
/*==============================================================*/
create table fnd_lookup_categories
(
   lookup_category_id   int not null auto_increment,
   category_code        varchar(100) not null,
   category_name        varchar(200) not null,
   category_locale      varchar(50) not null comment '字典locale',
   create_date          bigint not null,
   primary key (lookup_category_id)
);

alter table fnd_lookup_categories comment '数据字典category表, 支持多语言';

/*==============================================================*/
/* Table: fnd_lookup_values                                     */
/*==============================================================*/
create table fnd_lookup_values
(
   lookup_category_id   int not null auto_increment,
   lookup_code          varchar(100) not null,
   lookup_val           varchar(100),
   lookup_order         int comment '字典顺序 10 20 ... etc',
   create_date          bigint not null,
   primary key (lookup_category_id)
);

alter table fnd_lookup_values comment '数据字典value表';

/*==============================================================*/
/* Table: fnd_notice                                            */
/*==============================================================*/
create table fnd_notice
(
   notice_id            bigint not null auto_increment,
   notice_internal_id   varchar(50) not null comment '内部消息ID, 不区分语言. 使用uuid',
   user_id              bigint not null comment '通知接收人',
   notice_type          int not null comment '通知类型, 参考通知消息定义
            ',
   notice_content       varchar(600) not null comment '通知消息内容',
   notice_locale        varchar(50) not null comment '通知语言, 如zh_CN / en_US etc.',
   notice_read          int not null default 0 comment '消息是否已读 0 未读 1 已读',
   attr1                varchar(300),
   attr2                varchar(300),
   attr3                varchar(300),
   create_date          bigint not null,
   primary key (notice_id)
);

alter table fnd_notice comment '消息通知表, 支持多语言';

/*==============================================================*/
/* Table: fnd_tag_groups                                        */
/*==============================================================*/
create table fnd_tag_groups
(
   group_id             int not null auto_increment,
   group_title          varchar(100) not null comment '标签分组标题',
   group_locale         varchar(50) not null comment '文本语言, 支持国际化. 如: zh_CN / en_US',
   create_date          bigint not null,
   primary key (group_id)
);

alter table fnd_tag_groups comment '标签分组, 支持多语言';

/*==============================================================*/
/* Table: fnd_tags                                              */
/*==============================================================*/
create table fnd_tags
(
   tag_id               bigint not null auto_increment,
   tag_text             varchar(50) not null comment '标签文本',
   group_id             int comment '标签分组ID, 对于自定义标签该分组ID为空',
   create_date          bigint not null,
   primary key (tag_id)
);

alter table fnd_tags comment '标签';

/*==============================================================*/
/* Table: fnd_user_follow                                       */
/*==============================================================*/
create table fnd_user_follow
(
   uf_id                bigint not null auto_increment,
   user_id              bigint not null,
   user_followed        bigint not null comment '被关注的用户ID',
   create_date          bigint not null,
   primary key (uf_id)
);

alter table fnd_user_follow comment '用户关注';

/*==============================================================*/
/* Table: fnd_user_settings                                     */
/*==============================================================*/
create table fnd_user_settings
(
   user_tag_id          bigint not null auto_increment,
   user_id              bigint not null comment '标签文本, 冗余',
   setting_key          varchar(100) not null comment '设置key, 取自lookup',
   setting_val          varchar(100),
   create_date          bigint not null,
   primary key (user_tag_id)
);

alter table fnd_user_settings comment '用户设置';

/*==============================================================*/
/* Table: fnd_user_tags                                         */
/*==============================================================*/
create table fnd_user_tags
(
   user_tag_id          bigint not null auto_increment,
   user_id              bigint not null,
   tag_id               bigint not null,
   tag_text             varchar(200) not null comment '标签文本, 冗余',
   create_date          bigint not null,
   primary key (user_tag_id)
);

alter table fnd_user_tags comment '用户标签';

/*==============================================================*/
/* Table: poll_options                                          */
/*==============================================================*/
create table poll_options
(
   option_id            bigint not null auto_increment,
   poll_id              bigint not null,
   option_text          varchar(50) not null comment '选项',
   option_votes         bigint not null default 0 comment '投票数, 非实时更新',
   create_date          bigint not null,
   primary key (option_id)
);

alter table poll_options comment '投票数据';

/*==============================================================*/
/* Table: polls                                                 */
/*==============================================================*/
create table polls
(
   poll_id              bigint not null auto_increment,
   post_id              bigint not null,
   poll_duration        bigint not null comment '投票持续时间, 毫秒',
   poll_start           bigint not null comment '投票开始时间',
   poll_end             bigint not null comment '投票结束时间',
   create_date          bigint not null,
   primary key (poll_id)
);

alter table polls comment '投票表';

/*==============================================================*/
/* Table: post_comments                                         */
/*==============================================================*/
create table post_comments
(
   comment_id           bigint not null auto_increment,
   user_id              bigint not null,
   post_id              bigint not null comment '帖子id',
   comment_thread       varchar(50) comment '评论thread, 异步. 规则: 
            评论thread由各个层级由上往下最早的回复组成; 发表评论后, 首先判断父节点是否存在thread id(tid), 如果有则判断当前节点是否为最先的回复, 如果是则设置为父节点tid, 否则生成新的tid',
   comment_content      varchar(300) not null comment '帖子创建人',
   comment_imgs         varchar(300) comment '评论图片, 允许为空; 以json存储',
   mention_list         varchar(300) comment '@列表, 最多10个人; 如果超过, 使用最近的; 异步',
   comment_level        int comment '评论所在的层级, 异步',
   ancestor_comments    varchar(500) comment '父评论id数组, json格式保存; 异步',
   delete_mark          int not null default 0 comment '是否已删除 0 未删除 1 已删除',
   delete_reason        varchar(100) comment '删除原因',
   last_update          bigint comment '最后更新时间',
   update_by            bigint comment '更新人',
   create_date          bigint not null,
   primary key (comment_id)
);

alter table post_comments comment '帖子评论表';

/*==============================================================*/
/* Table: posts                                                 */
/*==============================================================*/
create table posts
(
   post_id              bigint not null auto_increment,
   user_id              bigint not null comment '帖子创建人',
   post_content         varchar(300) not null comment '文本语言, 支持国际化. 如: zh_CN / en_US',
   post_type            int not null comment '类型 1 基础 2 投票',
   post_imgs            varchar(300) comment '帖子图片, 最多允许3张. 以json字符串存储',
   delete_mark          int not null default 0 comment '是否已删除 0 未删除 1 已删除',
   delete_reason        varchar(100) comment '删除原因',
   last_update          bigint comment '最后更新时间',
   update_by            bigint comment '更新人',
   create_date          bigint not null,
   primary key (post_id)
);

alter table posts comment '帖子表';

/*==============================================================*/
/* Table: tut_attaches                                          */
/*==============================================================*/
create table tut_attaches
(
   tut_attach_id        bigint not null auto_increment,
   tut_id               bigint not null comment '帖子创建人',
   attach_title         varchar(50) not null comment '文本语言, 支持国际化. 如: zh_CN / en_US',
   attach_url           varchar(100) not null comment '类型 1 基础 2 投票',
   create_date          bigint not null,
   primary key (tut_attach_id)
);

alter table tut_attaches comment '教程附件表';

/*==============================================================*/
/* Table: tut_comments                                          */
/*==============================================================*/
create table tut_comments
(
   comment_id           bigint not null auto_increment,
   user_id              bigint not null,
   tut_id               bigint not null comment '帖子id',
   comment_thread       varchar(50) comment '评论thread',
   comment_content      varchar(300) not null comment '帖子创建人',
   delete_mark          int not null default 0 comment '是否已删除 0 未删除 1 已删除',
   delete_reason        varchar(100) comment '删除原因',
   last_update          bigint comment '最后更新时间',
   update_by            bigint comment '更新人',
   create_date          bigint not null,
   primary key (comment_id)
);

alter table tut_comments comment '教程评论';

/*==============================================================*/
/* Table: tutorials                                             */
/*==============================================================*/
create table tutorials
(
   tut_id               bigint not null auto_increment,
   tut_title            varchar(100) not null comment '帖子创建人',
   tut_intro            varchar(300) not null comment '文本语言, 支持国际化. 如: zh_CN / en_US',
   tut_short_list       varchar(600) not null comment '类型 1 基础 2 投票',
   tut_content_url      varchar(100) not null comment '教程内容(pdf / 视频)url',
   tut_code_url         varchar(200),
   tut_price            double(8, 2) not null,
   delete_mark          int not null default 0 comment '是否已删除 0 未删除 1 已删除',
   delete_reason        varchar(100) comment '删除原因',
   last_update          bigint comment '最后更新时间',
   update_by            bigint comment '更新人',
   create_date          bigint not null,
   primary key (tut_id)
);

alter table tutorials comment '教程表';

