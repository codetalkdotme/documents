
CREATE DATABASE cxdb DEFAULT CHARSET 'UTF8';

grant all on cxdb.* to 'cxdev'@'localhost' identified by 'welcome1';

flush privileges;