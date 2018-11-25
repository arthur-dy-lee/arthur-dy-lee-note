

# 一、mysql 安装



## 1、 sudo apt-get install mysql-server



安装过程中需要输入密码，也可以为空白，貌似默认值为: MYSQL



## 2、sudo netstat -tap | grep mysql



通过上述命令检查之后，如果看到有mysql 的socket处于 listen 状态则表示安装成功。

登陆mysql数据库可以通过如下命令：



## ３、mysql -u root -p

-u 表示选择登陆的用户名， -p 表示登陆的用户密码，上面命令输入之后会提示输入密码，此时输入密码就可以登录到mysql。



-u 表示选择登陆的用户名， -p 表示登陆的用户密码，上面命令输入之后会提示输入密码，此时输入密码就可以登录到mysql。



## 4、新建数据库

> create database lee;



## 5、创建用户并授权

GRANT ALL PRIVILEGES ON 数据库.* TO 用户名@"%" IDENTIFIED BY "用户密码";



>GRANT ALL PRIVILEGES ON lee.* TO lee@"%" IDENTIFIED BY "eeee";



刷新授权，不然授权在MySQL重启前不生效，执行这条指令后，即刻生效



>flush privileges;



# 二、 设置MySQL远程访问



## 1、 MySQL授权外部访问



## 2、取消127.0.0.1的监听绑定

>sudo gedit /etc/mysql/mysql.conf.d/mysqld.cnf;



在文件中：

>bind-address=0.0.0.0





## 3、开放防火墙端口



>sudo ufw allow 3306





# 三、试用



## 1、重启 MySQL

> sudo /etc/init.d/mysql restart



## 2、使用数据库lee

> use lee



## 3、创建个数据库表测试一下

```xml

create table tutorials_tbl(

   tutorial_id INT NOT NULL AUTO_INCREMENT,

   tutorial_title VARCHAR(100) NOT NULL,

   tutorial_author VARCHAR(40) NOT NULL,

   submission_date DATE,

   PRIMARY KEY ( tutorial_id )

);

```



## 4、主机安装个mysql链接一下试一下就可以了。

## 5、 修改字符集编码
### 查看字符集编码
SHOW VARIABLES LIKE 'char%';

### 在终端中输入命令
> sudo gedit /etc/mysql/mysql.conf.d/mysqld.cnf

打开mysqld.cnf 文件，在lc-messages-dir
= /usr/share/mysql 语句后添加语句
> character-set-server=utf8

### 在终端输入
> sudo gedit /etc/mysql/conf.d/mysql.cnf

命令打开mysql.cnf配置文件，如图添加代码：
> default-character-set=utf8

### 重启

> sudo /etc/init.d/mysql restart


## 查看mysql版本号
> mysql --version

## ubuntu mysql 主从复制
安装相同的mysql版本号
主: 192.168.192.172

从: 192.168.192.124
### 1.主

> sudo gedit /etc/mysql/my.cnf

文件中加入
```xml
[mysqld]
log-bin=mysql-bin
server-id=1
log-bin-index=master-bin.index
```
### 2.重启MySQL
参考：

[http://www.cnblogs.com/luxh/p/4088420.html](http://www.cnblogs.com/luxh/p/4088420.html)

[https://www.cnblogs.com/phpstudy2015-6/p/6687480.html#_label2](https://www.cnblogs.com/phpstudy2015-6/p/6687480.html#_label2)

### 3、从
> sudo gedit /etc/mysql/my.cnf

文件中加入
```xml

[mysqld]
server-id=2
relay-log-index=slave-relay-bin.index
relay-log=slave-relay-bin
```

### 4.在master上创建用于复制的账号
```xml
mysql> create user 'repl'@'%' identified by '123456';
Query OK, 0 rows affected (0.13 sec)

mysql> grant replication slave on *.* to 'repl'@'%' ;
Query OK, 0 rows affected (0.02 sec)
```

### 5. 在master上查看 binary log文件名和 position
```xml
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000003 |      120 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```

### 6. 配置slave

```xml
change master to master_host='192.168.192.174',master_user='repl',master_password='123456',master_port=3306,master_log_file='mysql-bin.000003',master_log_pos=120,master_connect_retry=10;
```
使用上面创建的用于复制的账号，master_log_file使用master上查询出来的文件名，master_log_pos也必须使用master上查询出来的position

### 7.在slave上启动复制功能
```xml
mysql> start slave;
Query OK, 0 rows affected (0.01 sec)
```

### 8.查看slave的状态
> mysql> show slave status

### 9、配置完毕。测试一下效果
```xml

mysql> create database web_db;
Query OK, 1 row affected (0.04 sec)

mysql> use web_db;
Database changed
mysql> create table t_user(id int primary key auto_increment,username varchar(32));
Query OK, 0 rows affected (0.13 sec)

mysql> create table t_dict(id int primary key auto_increment,username varchar(32));
Query OK, 0 rows affected (0.13 sec)


mysql> insert into t_user(username) values('arthurlee0320');
Query OK, 1 row affected (0.05 sec)

mysql> select * from t_user;
+----+----------+
| id | username |
+----+----------+
|  1 | lihuai   |
+----+----------+
1 row in set (0.00 sec)
```

在slave上查看，看是否已成功复制
```xml
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| test               |
| web_db             |
+--------------------+
5 rows in set (0.05 sec)

mysql> use web_db;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+------------------+
| Tables_in_web_db |
+------------------+
| t_user           |
+------------------+
1 row in set (0.00 sec)

mysql> select * from t_user;
+----+----------+
| id | username |
+----+----------+
|  1 | lihuai   |
+----+----------+
1 row in set (0.00 sec)
```

### ERROR 1872 (HY000): Slave failed to initialize relay log info structure from the repository

> reset slave;




echo "/usr/lib64/mysql" >> /etc/ld.so.conf.d/mysql-x86_64.conf
ldconfig

## mysql如何取消主从
在slave上

> mysql> stop slave;

> mysql> reset slave all;


## 启动 atlas
非常重要！！！
<font color=red size=6>
中间件atlas之前是基于mysql5.6编译的, 如果要使用mysql5.7，那么要重新编译Atlas
</font>

```xml
/usr/local/mysql-proxy/bin/mysql-proxy --defaults-file=/usr/local/mysql-proxy/conf/test.cnf
或
cd /usr/local/mysql-proxy/bin
/ysql-proxyd test start

或
/usr/local/mysql-proxy/bin/mysql-proxyd test start
```

## 查看是否启动成功
```xml
/usr/local/mysql-proxy/bin/mysql-proxyd test status
```

### 加密MySQL用户密码，获取的加密串填写到配置文件的pwds参数中。
```xml
/usr/local/mysql-proxy/bin/encrypt 123456

/iZxz+0GRoA=
```


### 管理
执行如下命令：
```xml
mysql -h127.0.0.1 -uuser -ppwd -P2345;
```

### 重要说明
```java
String url = "jdbc:mysql://192.168.1.244:1234/atlas_test";

// JDBC的URL,192.168.1.244为atlas的ip，端口是1234，atlas_test是主（写）库的实例名

Connection conn = DriverManager.getConnection(url, "root", "root");

//root/root是各个主从库的账号密码

```

### 重启
sudo ./mysql-proxyd test restart
### 停止
sudo ./mysql-proxyd test stop
### 查看状态
查看Atlas是否已经启动或停止

ps -ef | grep mysql-proxy


### 错误解决 /usr/local/mysql-proxy/bin/mysql-proxy: error while loading shared libraries: libcrypto.so.0.9.8: cannot open shared object file: No such file or directory error: failed to start MySQL-Proxy of test

分析依赖
> ldd mysql-proxy


查找目录下的
> sudo ls /lib/x86_64-linux-gnu/libssl*

会显示：
>/lib/x86_64-linux-gnu/libssl.so.1.0.0

然后

> sudo ln -s /lib/x86_64-linux-gnu/libcrypto.so.1.0.0 /lib/x86_64-linux-gnu/libcrypto.so.0.9.8

https://askubuntu.com/questions/833392/error-while-loading-shared-libraries-libssl-so-0-9-8-cannot-open-shared-object



### libmysqlclient.so.16: cannot open shared object file: No such file
```xml
2018-03-17 00:21:15: (critical) loading module '/usr/local/mysql-proxy/lib/mysql-proxy/plugins/libproxy.so' failed:
 libmysqlclient.so.16: cannot open shared object file: No such file or directory
2018-03-17 00:21:15: (critical) setting --plugin-dir=<dir> might help
2018-03-17 00:21:15: (message) Initiating shutdown, requested from mysql-proxy-cli.c:432
2018-03-17 00:21:15: (message) shutting down normally, exit code is: 1
```

sudo ls /lib/x86_64-linux-gnu/libmysqlclient*

> ls -l /usr/lib/mysql | grep libmysqlclient.so

找不到libmysqlclient.so.16的最终解决文案是安装了一个mysql-workbench


echo "/usr/lib/x86_64-linux-gnu/libmysqlclient.so.16" >> /etc/ld.so.conf
