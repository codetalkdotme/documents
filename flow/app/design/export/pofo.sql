/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/7/5 14:13:41                            */
/*==============================================================*/


drop table if exists pf_comments;

drop table if exists pf_posts;

drop table if exists pf_posts_imgs;

drop table if exists pf_tag_posts;

drop table if exists pf_tags;

drop table if exists pf_threads;

/*==============================================================*/
/* Table: pf_comments                                           */
/*==============================================================*/
create table pf_comments
(
   comment_id           bigint not null auto_increment,
   thread_id            bigint not null comment '帖子ID',
   post_id              bigint not null,
   user_id              int not null,
   comment_content      varchar(2000) not null,
   comment_order        bigint not null,
   reply_to             bigint comment '回复评论ID, 允许NULL',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (comment_id)
);

alter table pf_comments comment '帖子 评论 ';

/*==============================================================*/
/* Table: pf_posts                                              */
/*==============================================================*/
create table pf_posts
(
   post_id              bigint not null auto_increment,
   user_id              int not null,
   post_content         varchar(3000) not null,
   post_refer           bigint comment '应用的post ID, 允许NULL',
   post_likes           int not null default 0 comment '点赞数',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (post_id)
);

alter table pf_posts comment '帖子';

/*==============================================================*/
/* Table: pf_posts_imgs                                         */
/*==============================================================*/
create table pf_posts_imgs
(
   img_id               bigint not null auto_increment,
   post_id              bigint not null,
   img_url              varchar(200) not null comment '图片地址, fdfs地址',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (img_id)
);

alter table pf_posts_imgs comment '帖子图片';

/*==============================================================*/
/* Table: pf_tag_posts                                          */
/*==============================================================*/
create table pf_tag_posts
(
   tag_post_id          bigint not null auto_increment,
   tag_id               bigint not null,
   post_id              bigint not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (tag_post_id)
);

alter table pf_tag_posts comment '帖子 tag 映射的 post';

/*==============================================================*/
/* Table: pf_tags                                               */
/*==============================================================*/
create table pf_tags
(
   tag_id               bigint not null auto_increment,
   tag_text             varchar(200) not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (tag_id)
);

alter table pf_tags comment '帖子hash tag';

/*==============================================================*/
/* Table: pf_threads                                            */
/*==============================================================*/
create table pf_threads
(
   thread_id            bigint not null auto_increment,
   post_id              bigint not null comment '帖子ID',
   thread_order         bigint not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (thread_id)
);

alter table pf_threads comment '帖子 评论 - threads
POST 1
- Thread A 
    -- ';

