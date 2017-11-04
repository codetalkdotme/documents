/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/11/4 19:13:37                           */
/*==============================================================*/


drop table if exists sv_comments;

drop table if exists sv_ext_quests;

drop table if exists sv_quest_tags;

drop table if exists sv_quests;

drop table if exists sv_replies;

drop table if exists sv_reply_votes;

/*==============================================================*/
/* Table: sv_comments                                           */
/*==============================================================*/
create table sv_comments
(
   comment_id           bigint not null auto_increment,
   user_id              int not null,
   quest_id             bigint comment '回复答案时此字段为null',
   reply_id             bigint comment '回复问题时此字段为null',
   comment_reply        bigint,
   comment_content      varchar(2000),
   comment_seq          int,
   comment_thread       varchar(100) comment '评论thread, 使用UUID; 允许为null当评论quest时',
   notify_users         varchar(1000) comment '提醒的用户列表（冗余）, 示例: ["xx1", "xx2"], [] .
            
            此字段为异步填充,  如果为null则相应的消息未处理',
   spam_mark            int not null default 0 comment '是否垃圾评论',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
   update_by            int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (comment_id)
);

alter table sv_comments comment '问题评论, 答案评论, 评论的评论 不允许嵌套

问题1
- 评论1 - 评论2 -';

/*==============================================================*/
/* Table: sv_ext_quests                                         */
/*==============================================================*/
create table sv_ext_quests
(
   ext_quest_id         varchar(100) not null,
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
   primary key (ext_quest_id)
);

alter table sv_ext_quests comment '问题表(ext 外部)';

/*==============================================================*/
/* Table: sv_quest_tags                                         */
/*==============================================================*/
create table sv_quest_tags
(
   quest_tag_id         bigint not null auto_increment,
   quest_id             bigint not null,
   tag_id               int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (quest_tag_id)
);

alter table sv_quest_tags comment '问题标签';

/*==============================================================*/
/* Table: sv_quests                                             */
/*==============================================================*/
create table sv_quests
(
   quest_id             bigint not null auto_increment,
   quest_uuid           varchar(100) not null comment 'UUID',
   user_id              int not null,
   quest_title          varchar(200) not null,
   quest_content        longtext,
   quest_plus           int not null default 0 comment '我也有同样问题, +1 / 初始值1',
   quest_status         int not null comment '问题状态:
            1 - 草稿
            2 - 已发布
            3 - 冻结 (不允许回复)
            4 - 已解决',
   notify_users         varchar(1000) not null comment '提醒的用户列表（冗余）, 示例: ["xx1", "xx2"], [] .
            
            此字段为异步填充,  如果为null则相应的消息未处理',
   dup_mark             int not null default 0 comment '重复问题标记, 默认0
            0 不重复 
            1 重复 ',
   dup_qid              bigint comment '重复问题ID, 允许NULL',
   spam_mark            int not null default 0 comment '是否垃圾问题 0 否 1 是',
   quest_indexed        int not null default 0 comment '是否索引 0 否 1 是',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
   update_by            int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (quest_id)
);

alter table sv_quests comment '问题表';

/*==============================================================*/
/* Table: sv_replies                                            */
/*==============================================================*/
create table sv_replies
(
   reply_id             bigint not null auto_increment,
   quest_id             bigint not null,
   user_id              int not null,
   reply_content        longtext comment '我也有同样问题, +1 / 初始值1',
   reply_seq            int comment '回复的序号',
   reply_accepted       int not null default 0 comment '回答是否被接受, 一个问题只有一个被接受回复
            0 未接受
            1 接受',
   accept_date          timestamp comment '接受答复时间',
   comment_thread       varchar(100) not null comment '评论thread, 使用UUID',
   notify_users         varchar(1000) comment '提醒的用户列表（冗余）, 示例: ["xx1", "xx2"], [] .
            
            此字段为异步填充,  如果为null则相应的消息未处理',
   spam_mark            int not null default 0 comment '是否spam 0 否 1 是',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
   update_by            int not null,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (reply_id)
);

alter table sv_replies comment '问题答复表';

/*==============================================================*/
/* Table: sv_reply_votes                                        */
/*==============================================================*/
create table sv_reply_votes
(
   vote_id              bigint not null auto_increment,
   user_id              int not null comment '用户id',
   reply_id             bigint not null comment '答复id',
   vote_up              int not null comment '0 否 1 是',
   vote_down            int not null comment '0 否 1 是',
   delete_mark          int not null default 0 comment '是否已取消 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (vote_id)
);

alter table sv_reply_votes comment '答复vote';

