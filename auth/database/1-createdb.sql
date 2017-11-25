CREATE DATABASE authdb DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;;

grant all on authdb.* to 'authdb'@'%' identified by 'welcome5';

flush privileges;



