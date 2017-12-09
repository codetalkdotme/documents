/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/12/9 18:48:51                           */
/*==============================================================*/


drop table if exists d_stat_post;

drop table if exists d_stat_pv;

drop table if exists d_stat_reg;

drop table if exists d_stat_user_post;

drop table if exists d_stat_user_pv;

/*==============================================================*/
/* Table: d_stat_post                                           */
/*==============================================================*/
create table d_stat_post
(
   stat_date            varchar(20) not null comment '日期yyyymmdd格式',
   num_post             int not null,
   num_comment          int not null,
   num_post_like        int not null,
   num_cmnt_like        int not null comment '帖子/评论点赞',
   num_vote             int not null,
   create_date          bigint not null
);

alter table d_stat_post comment '发帖/评论/投票等统计';

/*==============================================================*/
/* Table: d_stat_pv                                             */
/*==============================================================*/
create table d_stat_pv
(
   stat_date            varchar(20) not null comment '日期yyyymmdd格式',
   num_user_pv          bigint not null comment '登录用户PV',
   num_guest_pv         int not null comment '游客用户PV',
   create_date          bigint not null
);

alter table d_stat_pv comment '总pv统计';

/*==============================================================*/
/* Table: d_stat_reg                                            */
/*==============================================================*/
create table d_stat_reg
(
   stat_date            varchar(20) not null comment '日期yyyymmdd格式',
   num_reg              int not null,
   create_date          bigint not null
);

alter table d_stat_reg comment '用户注册统计';

/*==============================================================*/
/* Table: d_stat_user_post                                      */
/*==============================================================*/
create table d_stat_user_post
(
   stat_date            varchar(20) not null comment '日期yyyymmdd格式',
   user_id              bigint not null comment '帖子创建人',
   num_post             int not null,
   num_comment          int not null,
   num_post_like        int not null,
   num_cmnt_like        int not null,
   num_vote             int not null,
   create_date          bigint not null
);

alter table d_stat_user_post comment '发帖/评论/投票等统计(按用户)';

/*==============================================================*/
/* Table: d_stat_user_pv                                        */
/*==============================================================*/
create table d_stat_user_pv
(
   stat_date            varchar(20) not null comment '日期yyyymmdd格式',
   user_id              bigint not null,
   num_pv               int not null,
   create_date          bigint not null
);

alter table d_stat_user_pv comment '用户pv统计';

