/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/11/25 21:24:31                          */
/*==============================================================*/


drop table if exists site_entity_attrs;

drop table if exists site_entity_types;

drop table if exists site_lists;

drop table if exists site_pages;

drop table if exists sites;

drop table if exists web_entities;

drop table if exists web_entity_attrs;

/*==============================================================*/
/* Table: site_entity_attrs                                     */
/*==============================================================*/
create table site_entity_attrs
(
   entity_type_id       int not null,
   attr_key             varchar(100) not null comment '站点名称',
   attr_el              varchar(300) not null comment '元素 路径',
   attr_name            varchar(100) comment '属性名称 null取text',
   attr_type            int comment '属性类型 1 html 2 text 3 html first 4 text first
            如果attr_name不为空, 则为null',
   create_date          timestamp not null default CURRENT_TIMESTAMP
);

alter table site_entity_attrs comment '站点entity 属性表';

/*==============================================================*/
/* Table: site_entity_types                                     */
/*==============================================================*/
create table site_entity_types
(
   entity_type_id       int not null auto_increment,
   site_id              int not null comment '站点名称',
   entity_type          varchar(100) not null comment '站点home页面url',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_type_id)
);

alter table site_entity_types comment '站点entity类型表';

/*==============================================================*/
/* Table: site_lists                                            */
/*==============================================================*/
create table site_lists
(
   list_id              int not null auto_increment,
   site_id              int not null comment '站点名称',
   entity_type_id       int not null,
   list_url             varchar(500) not null comment '列表页面URL, 包含page参数, 从1开始递增',
   list_type            int not null comment 'list类型 1 html 2 json 3 html - httpclient',
   list_priority        int not null default 1 comment '优先级 - 站点范围内, 越小优先级越高',
   list_pages_el        varchar(300) not null comment '子页面el, 支持page参数替换',
   list_pages_attr      varchar(100) comment '子页面属性',
   last_page            int not null comment '最后完成页 ',
   max_page             int not null comment '最大页码',
   page_param           varchar(100) comment '页面参数名称',
   list_enabled         int not null default 1 comment '是否启用 1 启用 0 不启用',
   error_msg            varchar(1000),
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (list_id)
);

alter table site_lists comment '站点列表配置';

/*==============================================================*/
/* Table: site_pages                                            */
/*==============================================================*/
create table site_pages
(
   page_id              varchar(100) not null,
   page_url             varchar(500) not null comment '站点名称',
   page_type            int not null comment 'page类型 1 html 2 html - httpclient',
   page_status          int not null default 1 comment '状态 1 未抓取 2 成功抓取 3 抓取出错',
   site_id              int not null,
   entity_type_id       int not null,
   error_msg            varchar(1000) comment '错误消息, 异常堆栈',
   last_update          timestamp not null default CURRENT_TIMESTAMP,
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (page_id)
);

alter table site_pages comment '站点页面列表, 记录状态';

/*==============================================================*/
/* Table: sites                                                 */
/*==============================================================*/
create table sites
(
   site_id              int not null auto_increment,
   site_name            varchar(100) not null comment '站点名称',
   site_home            varchar(200) not null comment '站点home页面url',
   site_logo            varchar(300) comment '站点logo url',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (site_id)
);

alter table sites comment '站点表';

/*==============================================================*/
/* Table: web_entities                                          */
/*==============================================================*/
create table web_entities
(
   entity_id            bigint not null auto_increment,
   site_id              int not null comment '所属站点ID',
   entity_type_id       int not null comment '实体类型ID',
   page_url             varchar(500) not null comment '页面完整URL',
   entity_indexed       int not null default 0 comment '页面是否被索引 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_id),
   key AK_uidx_page_url (page_url)
);

alter table web_entities comment 'entity表';

/*==============================================================*/
/* Table: web_entity_attrs                                      */
/*==============================================================*/
create table web_entity_attrs
(
   entity_attr_id       bigint not null auto_increment,
   entity_id            bigint not null,
   attr_key             varchar(100) not null comment '页面站点',
   attr_val             mediumtext comment '页面资源路径',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_attr_id)
);

alter table web_entity_attrs comment 'entity属性表';

