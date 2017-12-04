CREATE DATABASE flowstat DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;

grant all on flowstat.* to 'flowstat'@'%' identified by 'welcome7';

flush privileges;