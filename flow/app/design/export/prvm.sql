/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/7/5 14:14:48                            */
/*==============================================================*/


drop table if exists pm_messages;

/*==============================================================*/
/* Table: pm_messages                                           */
/*==============================================================*/
create table pm_messages
(
   mesg_id              bigint not null auto_increment,
   mesg_type            tinyint not null comment '消息类型 1 文字 2 图片',
   mesg_content         varchar(500) not null comment '消息内容, 支持文字 和 图片',
   from_user            int not null,
   to_user              int not null,
   mesg_thread          varchar(100) not null comment '由 fromUser 和 toUser组成, 比如fromUser=1001, toUser=100, 则thread=100-1001',
   mesg_status          tinyint not null default 1 comment '消息状态 1 未读 2 已读',
   spam_mark            tinyint not null default 0 comment '是否spam 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (mesg_id)
);

alter table pm_messages comment '私信消息';

