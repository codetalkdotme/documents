/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/9/22 19:50:10                           */
/*==============================================================*/


drop table if exists nt_notifs;

/*==============================================================*/
/* Table: nt_notifs                                             */
/*==============================================================*/
create table nt_notifs
(
   notif_id             bigint not null auto_increment,
   user_id              int not null comment '消息类型 1 文字 2 图片',
   notif_content        varchar(500) not null comment '消息内容, 支持文字 和 图片',
   notif_status         int not null default 0 comment '是否已读 1 未读 2 已读',
   last_update          timestamp not null default CURRENT_TIMESTAMP,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (notif_id)
);

alter table nt_notifs comment '通知消息';

