/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/11/7 11:00:45                           */
/*==============================================================*/


drop table if exists prs_articles;

/*==============================================================*/
/* Table: prs_articles                                          */
/*==============================================================*/
create table prs_articles
(
   article_id           int not null auto_increment,
   article_uuid         varchar(100) not null comment 'UUID',
   article_site         varchar(100) not null,
   article_url          varchar(300) not null comment '文章URL',
   article_title        varchar(500) not null,
   article_summary      varchar(1000),
   article_content      longtext,
   article_tags         varchar(200) default '0' comment '文章标签',
   article_indexed      int not null comment '是否索引 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (article_id)
);

alter table prs_articles comment '文章表';

