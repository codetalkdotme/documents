/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/5/19 17:57:09                           */
/*==============================================================*/


drop table if exists cx_questions;

drop table if exists cx_tutorials;

/*==============================================================*/
/* Table: cx_questions                                          */
/*==============================================================*/
create table cx_questions
(
   qst_id               bigint not null auto_increment,
   qst_url              varchar(500) not null,
   qst_title            varchar(500),
   qst_excerpt          varchar(1000),
   qst_tags             varchar(200) comment 'Tags, 使用JSON数组',
   qst_likes            int,
   qst_indexed          varchar(1) not null comment '是否已索引, Y / N',
   index_date           timestamp,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (qst_id)
);

alter table cx_questions comment '问题表';

/*==============================================================*/
/* Table: cx_tutorials                                          */
/*==============================================================*/
create table cx_tutorials
(
   tuto_id              bigint not null auto_increment,
   tuto_url             varchar(500) not null,
   tuto_title           varchar(500),
   tuto_excerpt         varchar(1000),
   tuto_series          varchar(1) not null,
   tuto_tags            varchar(200),
   tuto_likes           int,
   tuto_indexed         varchar(1) not null,
   index_date           timestamp,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (tuto_id)
);

alter table cx_tutorials comment 'Tutorials表';

