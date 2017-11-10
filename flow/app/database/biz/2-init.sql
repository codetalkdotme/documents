delete from fnd_lookups;

delete from fnd_tag_groups;
delete from fnd_tags;
delete from fnd_group_tags;
delete from fnd_position_tag_group;

-- lookups  1 开发 2 测试 3 产品 4 设计 5 运维
insert fnd_lookups (lookup_category, lookup_code, lookup_value, lookup_order) values
('POSITION_TYPE', '1', 'Developer', 10);
insert fnd_lookups (lookup_category, lookup_code, lookup_value, lookup_order) values
('POSITION_TYPE', '2', 'Testing', 20);
insert fnd_lookups (lookup_category, lookup_code, lookup_value, lookup_order) values
('POSITION_TYPE', '3', 'Product', 30);
insert fnd_lookups (lookup_category, lookup_code, lookup_value, lookup_order) values
('POSITION_TYPE', '4', 'Design', 40);
insert fnd_lookups (lookup_category, lookup_code, lookup_value, lookup_order) values
('POSITION_TYPE', '5', 'DevOps', 50);

-- tags
insert into fnd_tags (tag_id, tag_text) values (101, 'J2SE');
insert into fnd_tags (tag_id, tag_text) values (102, 'Spring-MVC');
insert into fnd_tags (tag_id, tag_text) values (103, 'Spring-Boot');
insert into fnd_tags (tag_id, tag_text) values (104, 'MyBatis');

insert into fnd_tags (tag_id, tag_text) values (201, 'Bash');
insert into fnd_tags (tag_id, tag_text) values (202, 'Sed');
insert into fnd_tags (tag_id, tag_text) values (203, 'Awk');

insert into fnd_tags (tag_id, tag_text) values (301, 'Oracle');
insert into fnd_tags (tag_id, tag_text) values (302, 'MySQL');
insert into fnd_tags (tag_id, tag_text) values (303, 'Perf-Tuning');

insert into fnd_tags (tag_id, tag_text) values (401, 'Redis');
insert into fnd_tags (tag_id, tag_text) values (402, 'Memcache');

insert into fnd_tags (tag_id, tag_text) values (501, 'Loadrunner');
insert into fnd_tags (tag_id, tag_text) values (502, 'Jmeter');
insert into fnd_tags (tag_id, tag_text) values (503, 'Gatling');

insert into fnd_tags (tag_id, tag_text) values (601, 'AxureRP');
insert into fnd_tags (tag_id, tag_text) values (602, 'Mockplus');
insert into fnd_tags (tag_id, tag_text) values (603, 'Balsamiq-Mockups');
insert into fnd_tags (tag_id, tag_text) values (604, 'Photoshop');
insert into fnd_tags (tag_id, tag_text) values (605, 'Illustrator');
insert into fnd_tags (tag_id, tag_text) values (606, 'Sketch');

insert into fnd_tags (tag_id, tag_text) values (701, 'Tomcat');
insert into fnd_tags (tag_id, tag_text) values (702, 'Jetty');
insert into fnd_tags (tag_id, tag_text) values (703, 'Undertow');
insert into fnd_tags (tag_id, tag_text) values (704, 'Nginx');
insert into fnd_tags (tag_id, tag_text) values (705, 'Squid');

-- group
insert into fnd_tag_groups (group_id, group_title, group_desc) values (1, 'Java', 'Java');
insert into fnd_group_tags (group_id, tag_id) values (1, 101);
insert into fnd_group_tags (group_id, tag_id) values (1, 102);
insert into fnd_group_tags (group_id, tag_id) values (1, 103);
insert into fnd_group_tags (group_id, tag_id) values (1, 104);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (2, 'Linux', 'Linux');
insert into fnd_group_tags (group_id, tag_id) values (2, 201);
insert into fnd_group_tags (group_id, tag_id) values (2, 202);
insert into fnd_group_tags (group_id, tag_id) values (2, 203);


insert into fnd_tag_groups (group_id, group_title, group_desc) values (3, 'Database', 'Database');
insert into fnd_group_tags (group_id, tag_id) values (3, 301);
insert into fnd_group_tags (group_id, tag_id) values (3, 302);
insert into fnd_group_tags (group_id, tag_id) values (3, 303);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (4, 'Cache', 'cache');
insert into fnd_group_tags (group_id, tag_id) values (4, 401);
insert into fnd_group_tags (group_id, tag_id) values (4, 402);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (5, 'Testing tools', 'Testing tools');
insert into fnd_group_tags (group_id, tag_id) values (5, 501);
insert into fnd_group_tags (group_id, tag_id) values (5, 502);
insert into fnd_group_tags (group_id, tag_id) values (5, 503);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (6, 'Prototype design', 'Prototype design');
insert into fnd_group_tags (group_id, tag_id) values (6, 601);
insert into fnd_group_tags (group_id, tag_id) values (6, 602);
insert into fnd_group_tags (group_id, tag_id) values (6, 603);
insert into fnd_group_tags (group_id, tag_id) values (6, 606);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (7, 'Creative design', 'Prototype design');
insert into fnd_group_tags (group_id, tag_id) values (7, 604);
insert into fnd_group_tags (group_id, tag_id) values (7, 605);
insert into fnd_group_tags (group_id, tag_id) values (7, 606);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (8, 'Web servers', 'Web servers');
insert into fnd_group_tags (group_id, tag_id) values (8, 701);
insert into fnd_group_tags (group_id, tag_id) values (8, 702);
insert into fnd_group_tags (group_id, tag_id) values (8, 703);

insert into fnd_tag_groups (group_id, group_title, group_desc) values (9, 'Proxy & Cache', 'Proxy & Cache');
insert into fnd_group_tags (group_id, tag_id) values (9, 704);
insert into fnd_group_tags (group_id, tag_id) values (9, 705);

-- position tag group
-- Dev
insert into fnd_position_tag_group (position_type, tag_group_id) values (1, 1);
insert into fnd_position_tag_group (position_type, tag_group_id) values (1, 2);
insert into fnd_position_tag_group (position_type, tag_group_id) values (1, 3);
insert into fnd_position_tag_group (position_type, tag_group_id) values (1, 4);
insert into fnd_position_tag_group (position_type, tag_group_id) values (1, 8);

-- Test
insert into fnd_position_tag_group (position_type, tag_group_id) values (2, 5);

-- Product
insert into fnd_position_tag_group (position_type, tag_group_id) values (3, 6);

-- Design
insert into fnd_position_tag_group (position_type, tag_group_id) values (4, 7);

-- DevOps
insert into fnd_position_tag_group (position_type, tag_group_id) values (5, 8);
insert into fnd_position_tag_group (position_type, tag_group_id) values (5, 9);




















