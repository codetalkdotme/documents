-- site
delete from sites;

insert into sites (site_id, site_name, site_home, site_logo) values (1, 'stackoverflow', 'https://stackoverflow.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (2, 'dzone', 'https://dzone.com', null);

-- site entities
delete from site_entity_types;

insert into site_entity_types(entity_type_id, site_id, entity_type) values (101, 1, 'quest');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (201, 2, 'article');

-- entity attrs 
delete from site_entity_attrs;

-- stackoverflow
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_title", 1, "#question-header h1 a", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_votes", 1, "#question span.vote-count-post", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_content", 1, "#question div.post-text", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_tags", 2, "#question div.post-taglist a", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_accepted", 1, "#answers span.vote-accepted-on", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_answer", 1, "#answers div.accepted-answer div.post-text", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (101, "quest_top_reply", 3, "#answers div.answer div.post-text", null);

-- dzone
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_title", 1, "article h1.article-title", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_summary", 1, "article div.subhead h3", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_content", 1, "article div[itemprop=articleBody]", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_tags", 2, "div.article-topics span.topics-tag", null);

-- site lists
delete from site_lists;

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/java?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page');
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=spring+boot&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage');
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/spring?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page');

























