/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/7/5 14:14:30                            */
/*==============================================================*/


drop table if exists sv_questions;

drop table if exists sv_replies;

drop table if exists sv_reply_comments;

/*==============================================================*/
/* Table: sv_questions                                          */
/*==============================================================*/
create table sv_questions
(
   quest_id             bigint not null auto_increment,
   user_id              int not null,
   quest_title          varchar(200) not null,
   quest_content        longtext,
   quest_plus           int not null comment '我也有同样问题, +1 / 初始值1',
   quest_status         tinyint not null comment '问题状态:
            1 - 草稿
            2 - 已发布
            3 - 冻结 (不允许回复)
            4 - 已解决',
   dup_mark             tinyint not null default 0 comment '重复问题标记, 默认0
            0 不重复 
            1 重复 ',
   dup_qid              bigint comment '重复问题ID, 允许NULL',
   spam_mark            tinyint not null default 0 comment '是否垃圾问题 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (quest_id)
);

alter table sv_questions comment '问题表';

/*==============================================================*/
/* Table: sv_replies                                            */
/*==============================================================*/
create table sv_replies
(
   reply_id             bigint not null auto_increment,
   quest_id             bigint not null,
   user_id              int not null,
   reply_content        longtext comment '我也有同样问题, +1 / 初始值1',
   reply_ups            int not null default 0,
   reply_downs          int not null default 0,
   reply_accepted       tinyint not null comment '回答是否被接受, 一个问题只有一个被接受回复
            0 未接受
            1 接受',
   spam_mark            tinyint not null default 0 comment '是否spam 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (reply_id)
);

alter table sv_replies comment '问题回复表';

/*==============================================================*/
/* Table: sv_reply_comments                                     */
/*==============================================================*/
create table sv_reply_comments
(
   comment_id           bigint not null auto_increment,
   reply_id             bigint not null,
   comment_content      varchar(2000) comment '我也有同样问题, +1 / 初始值1',
   comment_replyto      bigint comment '回复给comment ID, 允许NULL',
   comment_order        bigint not null,
   spam_mark            tinyint not null default 0 comment '是否垃圾评论',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (comment_id)
);

alter table sv_reply_comments comment '问题回复的评论';

