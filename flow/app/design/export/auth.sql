/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/7/5 14:04:51                            */
/*==============================================================*/


drop table if exists auth_logins;

drop table if exists auth_tickets;

drop table if exists auth_users;

/*==============================================================*/
/* Table: auth_logins                                           */
/*==============================================================*/
create table auth_logins
(
   login_id             bigint not null auto_increment,
   user_id              int not null,
   client_ip            varchar(50),
   client_platform      varchar(50),
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (login_id)
);

alter table auth_logins comment '登录信息表';

/*==============================================================*/
/* Table: auth_tickets                                          */
/*==============================================================*/
create table auth_tickets
(
   ticket_id            bigint not null auto_increment,
   login_id             bigint not null,
   login_name           varchar(200) not null comment '用户登录名',
   transport_key        varchar(50) not null comment '传输密钥 - 32位16进制, 示例: "1044DE5E7235BEE6DB490302D464AD61"',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (ticket_id)
);

alter table auth_tickets comment '服务票据表';

/*==============================================================*/
/* Table: auth_users                                            */
/*==============================================================*/
create table auth_users
(
   user_id              int not null auto_increment,
   user_login           varchar(200) not null comment '用户登录名, 由英文字母 和 下划线组成',
   user_name            varchar(200) not null comment '用户名称, 允许中文 / 空格等',
   user_profile         varchar(200) not null comment '用户头像URL, 存放fdfs路径, 示例: "group1/M00/00/03/wKhQA1lbBFaALuwvAAChr-JPsVI9607165"',
   profile_status       tinyint not null comment '头像状态
            1. 待审核
            2. 审核通过
            3. 拒绝',
   user_mail            varchar(50) not null comment '文件类型',
   mail_verified        TINYINT not null comment '是否验证email
            0 未验证
            1 已验证',
   user_status          tinyint not null comment '账户状态
            1. active 
            2. suspend 停用状态',
   user_passwd          varchar(50) not null comment '用户密码, 存放变换后的密码: HEX(MD5("8!9HA3D6GB2A4mb2+实际输入的密码+9Cx8BB@A41B59658"))
            ',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (user_id)
);

alter table auth_users comment '用户表';

