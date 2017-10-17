/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/9/25 13:20:41                           */
/*==============================================================*/


drop table if exists pf_comments;

drop table if exists pf_follows;

drop table if exists pf_post_imgs;

drop table if exists pf_post_likes;

drop table if exists pf_posts;

/*==============================================================*/
/* Table: pf_comments                                           */
/*==============================================================*/
create table pf_comments
(
   comment_id           bigint not null auto_increment,
   post_id              bigint comment '回复帖子时非NULL，回复评论时为NULL',
   user_id              int not null,
   comment_content      varchar(2000) not null,
   comment_reply        bigint comment '回复评论ID, 允许NULL',
   comment_seq          int comment '评论序号',
   notify_users         varchar(1000) comment '提醒的用户列表（冗余）, 示例: ["xx1", "xx2"], [] .
            
            此字段为异步填充,  如果为null则相应的消息未处理',
   comment_thread       varchar(100) not null comment '评论thread
            允许最多两层, 第一层的评论生成thread, 所有第二层的回复使用该thread',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (comment_id)
);

alter table pf_comments comment '帖子 评论，允许最多两层

帖子1
- 评论1 
-- 评论11 - 评';

/*==============================================================*/
/* Table: pf_follows                                            */
/*==============================================================*/
create table pf_follows
(
   follow_id            bigint not null auto_increment,
   user_id              int not null,
   user_followed        int not null,
   delete_mark          int not null default 0 comment '是否删除(已取消关注)
            0 未删除 1 已删除',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (follow_id)
);

alter table pf_follows comment '关注表';

/*==============================================================*/
/* Table: pf_post_imgs                                          */
/*==============================================================*/
create table pf_post_imgs
(
   img_id               bigint not null auto_increment,
   post_id              bigint not null,
   img_url              varchar(200) not null comment '图片地址, fdfs地址',
   img_seq              int not null comment 'image序号',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (img_id)
);

alter table pf_post_imgs comment '帖子图片';

/*==============================================================*/
/* Table: pf_post_likes                                         */
/*==============================================================*/
create table pf_post_likes
(
   like_id              bigint not null auto_increment,
   post_id              bigint not null,
   user_id              int not null,
   delete_mark          int not null default 0 comment '删除标记 0 未删除 1 已删除',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (like_id)
);

alter table pf_post_likes comment '帖子点赞';

/*==============================================================*/
/* Table: pf_posts                                              */
/*==============================================================*/
create table pf_posts
(
   post_id              bigint not null auto_increment,
   user_id              int not null,
   post_type            int not null comment '帖子类型
            1、帖子（原创）
            2、转帖',
   post_content         varchar(3000) comment '转贴时评论内容允许NULL',
   post_id_refer        bigint comment '应用的post ID, 允许NULL',
   comment_id_refer     bigint comment '转帖评论 或者 评论作为帖子',
   notify_users         varchar(1000) comment '提醒的用户列表（冗余）, 示例: ["xx1", "xx2"], [] .
            
            此字段为异步填充,  如果为null则相应的消息未处理',
   post_refers          int not null default 0 comment '转帖数量：repost + quote',
   post_likes           int not null default 0 comment '点赞数',
   post_comments        int not null default 0 comment '评论数量',
   post_views           bigint not null default 0 comment '查看详情次数',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (post_id)
);

alter table pf_posts comment '帖子';

