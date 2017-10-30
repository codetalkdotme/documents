/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/10/30 21:27:14                          */
/*==============================================================*/


drop table if exists site_entities;

drop table if exists site_entity_attrs;

drop table if exists site_lists;

drop table if exists site_pages;

drop table if exists sites;

drop table if exists web_entities;

drop table if exists web_entity_attrs;

/*==============================================================*/
/* Table: site_entities                                         */
/*==============================================================*/
create table site_entities
(
   entity_id            int not null auto_increment,
   site_id              int not null comment '站点名称',
   entity_name          varchar(100) not null comment '站点home页面url',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_id)
);

alter table site_entities comment '站点entity表';

/*==============================================================*/
/* Table: site_entity_attrs                                     */
/*==============================================================*/
create table site_entity_attrs
(
   entity_id            int not null auto_increment,
   attr_key             varchar(100) not null comment '站点名称',
   attr_el              varchar(300) not null comment '元素 路径',
   attr_name            varchar(100) comment '属性名称 null取text',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_id)
);

alter table site_entity_attrs comment '站点entity 属性表';

/*==============================================================*/
/* Table: site_lists                                            */
/*==============================================================*/
create table site_lists
(
   config_id            int not null auto_increment,
   site_id              int not null comment '站点名称',
   entity_id            int not null,
   list_url             varchar(500) not null comment '列表页面URL, 包含page参数, 从1开始递增',
   list_type            int not null comment 'list类型 1 html 2 json',
   list_pages_el        varchar(300) not null comment '子页面el, 支持page参数替换',
   list_pages_attr      varchar(100) comment '子页面属性',
   last_page            int not null comment '最后完成页 ',
   list_done            int not null default 0 comment '是否完成 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (config_id)
);

alter table site_lists comment '站点列表配置';

/*==============================================================*/
/* Table: site_pages                                            */
/*==============================================================*/
create table site_pages
(
   page_id              int not null auto_increment,
   page_url             varchar(500) not null comment '站点名称',
   page_status          int not null default 0 comment '状态 1 未抓取 2 成功抓取 3 抓取出错',
   update_date          timestamp not null default CURRENT_TIMESTAMP,
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
   site_logo            varchar(300) not null comment '站点logo url',
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
   page_url             varchar(500) not null comment '页面完整URL',
   entity_indexed       int not null comment '页面是否被索引 0 否 1 是',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_id)
);

alter table web_entities comment 'entity表';

/*==============================================================*/
/* Table: web_entity_attrs                                      */
/*==============================================================*/
create table web_entity_attrs
(
   entity_attr_id       bigint not null auto_increment,
   attr_key             varchar(100) not null comment '页面站点',
   attr_val             text not null comment '页面资源路径',
   create_date          timestamp not null default CURRENT_TIMESTAMP,
   primary key (entity_attr_id)
);

alter table web_entity_attrs comment 'entity属性表';

