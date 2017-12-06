/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/12/6 17:04:08                           */
/*==============================================================*/


drop table if exists auth_users;

/*==============================================================*/
/* Table: auth_users                                            */
/*==============================================================*/
create table auth_users
(
   user_id              bigint not null auto_increment,
   user_login           varchar(30) not null comment '用户登录名',
   user_name            varchar(200) comment '用户名: 真实姓名或者昵称',
   user_profile         varchar(200) comment '用户头像url',
   user_passwd          varchar(50) not null comment '用户密码',
   user_signature       varchar(300) comment '用户个性签名',
   user_mobile          varchar(30) comment '用户手机号码',
   mobile_verified      int not null default 0 comment '手机号码是否验证 0 否 1 是',
   user_mail            varchar(100) comment '用户邮箱',
   mail_verified        int not null default 0 comment '邮箱是否验证 0 否 1 是',
   user_disabled        int not null default 0 comment '用户是否禁用 0 否 1 是',
   disable_reason       varchar(200) comment '禁用用户的原因',
   attr1                varchar(300),
   attr2                varchar(300),
   attr3                varchar(300),
   last_update          bigint comment '最后更新时间',
   update_by            bigint comment '最后更新人',
   create_date          bigint not null,
   primary key (user_id)
);

alter table auth_users comment '用户表';

