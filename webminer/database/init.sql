-- site
delete from sites;

insert into sites (site_id, site_name, site_home, site_logo) values (1, 'stackoverflow', 'https://stackoverflow.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (2, 'dzone', 'https://dzone.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (3, 'javacodegeeks', 'https://www.javacodegeeks.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (4, 'baeldung', 'http://www.baeldung.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (5, 'mkyong', 'http://www.mkyong.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (6, 'infoq', 'https://www.infoq.com', null);
insert into sites (site_id, site_name, site_home, site_logo) values (7, 'thenewstack', 'https://thenewstack.io/', null);
insert into sites (site_id, site_name, site_home, site_logo) values (8, 'jvns', 'https://jvns.ca/', null);
insert into sites (site_id, site_name, site_home, site_logo) values (9, 'martinfowler', 'https://martinfowler.com/', null);

-- site entities
delete from site_entity_types;

insert into site_entity_types(entity_type_id, site_id, entity_type) values (101, 1, 'quest');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (201, 2, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (301, 3, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (401, 4, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (501, 5, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (601, 6, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (701, 7, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (801, 8, 'article');
insert into site_entity_types(entity_type_id, site_id, entity_type) values (901, 9, 'article');

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
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_title", 1, "article div.title h1.article-title", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_summary", 1, "article div.subhead h3", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_content", 1, "article div[itemprop=articleBody]", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (201, "article_tags", 2, "div.article-topics span.topics-tag", null);

-- javacodegeeks
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (301, "article_title", 2, "article#the-post h1.post-title span", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (301, "article_summary", 3, "article#the-post div.entry > p", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (301, "article_content", 1, "article#the-post div.entry", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (301, "article_tags", 1, "div#main-content div.content p.post-tag", null);

-- baeldung
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (401, "article_title", 2, "article.post div.page-header h1.entry-title", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (401, "article_content", 1, "article.post section.post_content", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (401, "article_tags", 1, "article.post div.page-header ul.categories li", null);

-- mkyong
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (501, "article_title", 4, "div#post-container article h1", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (501, "article_summary", 3, "div#post-container article div.post-content > p", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (501, "article_content", 1, "div#post-container article div.post-content", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (501, "article_tags", 1, "div#post-container article span.post-tag", null);

-- infoq
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (601, "article_title", 3, "div#site div#content h1.general", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (601, "article_summary", 3, "div#site div#content div.text_info > p", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (601, "article_content", 1, "div#site div#content div.text_info", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (601, "article_tags", 1, "div#site div#content div.text_info div.related ul li a.followable", null);

-- thenewstack
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (701, "article_title", 3, "#main article header.entry-header h1", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (701, "article_summary", 3, "#main article div.entry-content div.post-content > p", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (701, "article_content", 1, "#main article div.entry-content div.post-content", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (701, "article_tags", 1, "#main article footer.entry-footer div.newtags", null);

-- jvns
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (801, "article_title", 3, "#main article header h1.entry-title", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (801, "article_summary", 3, "#main article div.entry-content > p", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (801, "article_content", 1, "#main article div.entry-content", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (801, "article_tags", 1, "#main article header div.post-tags", null);

-- martinfowler
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (901, "article_title", 3, "div#content div.pattern h1", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (901, "article_summary", 1, "div#content div.pattern p.intent", null);
insert into site_entity_attrs (entity_type_id, attr_key, attr_type, attr_el, attr_name) values (901, "article_content", 1, "div#content div.pattern", null);

-- site lists
delete from site_lists;

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/java?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 1000);
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=spring+boot&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 1000);
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/spring?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 1000);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/mysql?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 1000);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=java+concurrency&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 1000);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/page/{page}/', 
		3, 'div.post-listing article h2.post-title a', 'href', 0, 'page', 100000);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/rest-with-spring-series/', 
		3, 'section.post_content ul li a', 'href', 0, null, 1);
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/persistence-with-spring-series', 
		3, 'section.post_content h3 a', 'href', 0, null, 1);
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/security-spring', 
		3, 'section.post_content ul li a', 'href', 0, null, 1);
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/spring-exceptions', 
		3, 'section.post_content h3 a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/jackson', 
		3, 'section.post_content ul li a', 'href', 0, null, 1);
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/httpclient-guide', 
		3, 'section.post_content ul li a', 'href', 0, null, 1);
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (4, 401, 'http://www.baeldung.com/java-tutorial', 
		3, 'section.post_content h4 a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2015/09/advanced-java.html', 
		3, 1, 'article#the-post div.entry h3 a', 'href', 0, null, 1);
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2015/09/apache-lucene-fundamentals.html', 
		3, 2, 'article#the-post div.entry h3 a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2015/09/mongodb-a-scalable-nosql-db.html', 
		3, 3, 'article#the-post div.entry h3 a', 'href', 0, null, 1);						
			
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2015/09/java-concurrency-essentials.html', 
		3, 4, 'article#the-post div.entry h3 a', 'href', 0, null, 1);	

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2015/09/java-design-patterns.html', 
		3, 5, 'article#the-post div.entry h3 a', 'href', 0, null, 1);	
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_priority, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (3, 301, 'https://www.javacodegeeks.com/2017/04/elasticsearch-tutorial-java-developers.html', 
		3, 6, 'article#the-post div.entry h3 a', 'href', 0, null, 1);		


insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/search?tab=relevance&q=java%20concurrency&page={page}', 1, 'div.search-results div.question-summary div.result-link a', 'href', 0, 'page', 1000);


insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/oracle?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 400);

-- mkyong
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-8-tutorials/', 3, 'article div.post-content div.tut-content ol li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/spring-boot-tutorials/', 3, 'article div.post-content div.tut-content ol li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/spring-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/spring-mvc-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/spring-security-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/spring-batch-tutorial/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-xml-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-json-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-date-time-tutorials/', 3, 'article div.post-content > ol li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-regular-expression-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-io-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/jdbc-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/hibernate-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/java-mongodb-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/maven-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/log4j-tutorial/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (5, 501, 'http://www.mkyong.com/tutorials/junit-tutorials/', 3, 'article div.post-content > ul li a', 'href', 0, null, 1);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=mysql&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 100);
		
insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=spring%20cloud&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=redis&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=scala&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/redis?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 400);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/apache-kafka?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 60);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/rabbitmq?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 100);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/bash?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 360);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/sed?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 200);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/awk?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 250);


insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=docker&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 50);


insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=kafka&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=rabbitmq&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 20);


insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/eureka?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/hystrix?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/feign?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 20);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (1, 101, 'https://stackoverflow.com/questions/tagged/docker?sort=votes&page={page}', 1, 'div#questions div.summary h3 a', 'href', 0, 'page', 400);

insert into site_lists (site_id, entity_type_id, list_url, list_type, list_pages_el, list_pages_attr, last_page, page_param, max_page) 
values (2, 201, 'https://dzone.com/services/widget/header-headerV2/nextPage?maxSize=10&numPages=1&pageSize=50&term=spring%20mvc&totalItems=0&currentPage={currentPage}', 
		2, 'result data pages newest {currentPage}', 'url', 0, 'currentPage', 100);











