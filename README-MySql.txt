mysql> show grants;
+---------------------------------------------------------------------+
| Grants for root@localhost                                           |
+---------------------------------------------------------------------+
| GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION |
| GRANT PROXY ON ''@'' TO 'root'@'localhost' WITH GRANT OPTION        |
+---------------------------------------------------------------------+
2 rows in set (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION 

mysql> use mysql;
mysql> SET PASSWORD FOR 'root'@'%' = PASSWORD('abcd1234');


CREATE DATABASE tokenizer CHARACTER SET utf8 COLLATE utf8_bin;

##grant all privileges on tokenizer.* to 'tokenizer'@'localhost' identified by 'sentitwittermap' WITH GRANT OPTION;

grant all privileges on tokenizer.* to 'tokenizer'@'localhost' WITH GRANT OPTION;