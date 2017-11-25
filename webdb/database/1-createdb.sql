CREATE DATABASE webdb DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;;

grant all on webdb.* to 'webdb'@'%' identified by 'welcome5';

flush privileges;



