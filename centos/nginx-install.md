# nginx 安装



## 一、安装方法1：直接用命令安装



### 1.1 添加Nginx存储库

要添加CentOS 7 EPEL仓库，请打开终端并使用以下命令： 

> sudo yum install epel-release 



### 1.2 安装Nginx

现在Nginx存储库已经安装在您的服务器上，使用以下`yum`命令安装Nginx ： 

> sudo yum install nginx 



在对提示回答yes后，Nginx将在服务器上完成安装。 



### 1.3 启动

Nginx不会自行启动。要运行Nginx，请输入： 

> sudo systemctl start nginx 



如果您正在运行防火墙，请运行以下命令以允许HTTP和HTTPS通信： 

> sudo firewall-cmd --permanent --zone=public --add-service=http 
>
> sudo firewall-cmd --permanent --zone=public --add-service=https 
>
> sudo firewall-cmd --reload 

访问 http://localhost , 您将会看到默认的CentOS 7 Nginx网页 

![](https://farm1.staticflickr.com/855/42989719944_cb7ee2de0a_o.png[/img][/url])

### 1.4 开机启动

如果想在系统启动时启用Nginx。请输入以下命令：

> sudo systemctl enable nginx



## 1.7 查看nginx正在运行的进程

>ps -ef |grep nginx 



### 1.6 卸载

>sudo apt-get --purge remove nginx 

 自动移除全部不使用的软件包

> sudo apt-get autoremove 

罗列出与nginx相关的软件

>dpkg --get-selections|grep nginx 



##　安装方法2：绿色安装



### 2.1  下载 nginx-1.14.0.tar.gz

[http://nginx.org/en/download.html](http://nginx.org/en/download.html)



### 2.2安装前的准备工作

>yum install gcc-c++  
 yum install pcre pcre-devel  
 yum install zlib zlib-devel  
 yum install openssl openssl--devel  






### 2.3复制文件到 /usr/local



### 2.4 解压文件nginx-1.14.0.tar.gz

> sudo tar zxvf nginx-1.14.0.tar.gz



### 2.5 进入/usr/local/nginx-1.14.0 目录


### 2.6 执行命令编译

>./configure 

./configure 默认安装到 /lusr/local/nginx

### 2.7 安装
>make && make install 



### 2.8 启动、重启、停止

启动 

> /usr/local/nginx/sbin/nginx

重启

> /usr/local/nginx/sbin/nginx -s reload

停止 

> /usr/local/nginx/sbin/nginx –s stop 

测试配置文件是否正常 

> /usr/local/nginx/sbin/nginx –t 

强制关闭 

>pkill nginx 



## 三、tomcat相关配置

### 3.1 配置

> vi /usr/local/nginx/conf/nginx.conf

如下图所示

![](https://farm1.staticflickr.com/861/43658819842_0e1ac4605e_o.png)





## 四、双tomcat配置





防火墙中配置开放 8080端口

> firewall-cmd --zone=public --add-port=8080/tcp --permanent 





## 五、关闭防火墙

> systemctl stop firewalld.service             #停止
>
> firewall systemctl disable firewalld.service        #禁止firewall开机启动 



### 5.1 开放端口

> firewall-cmd --zone=public --add-port=80/tcp --permanent 



### 5.2 重启防火墙

> firewall-cmd --reload 



查看已经开放的端口 

> firewall-cmd --list-ports 



