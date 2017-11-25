/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/11/25 21:35:17                          */
/*==============================================================*/


drop table if exists web_articles;

drop table if exists web_quests;

/*==============================================================*/
/* Table: web_articles                                          */
/*==============================================================*/
create table web_articles
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

alter table web_articles comment '文章表';

/*==============================================================*/
/* Table: web_quests                                            */
/*==============================================================*/
create table web_quests
(
   quest_id             bigint not null auto_increment,
   quest_uuid           varchar(100) not null,
   quest_site           varchar(100) not null comment '站点',
   quest_url            varchar(300) not null,
   quest_title          varchar(500) not null comment '标题',
   quest_content        longtext,
   quest_answer         longtext not null,
   quest_tags           varchar(200) default '0' comment '问题标签',
   quest_votes          int not null,
   answer_accepted      int not null comment '是否被接受 0 否 1 是',
   quest_indexed        int not null default 0 comment '是否索引 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (quest_id)
);

alter table web_quests comment '问题表(ext 外部)';

