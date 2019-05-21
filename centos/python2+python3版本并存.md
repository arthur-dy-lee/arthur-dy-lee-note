## 一、解决Python2 pip问题

----

在centos7中安装好操作系统，自带的是Python2的版本，但是并没有pip的方法，我们需要自行安装 报名为python-pip



```bash
# 默认python2的版本
[root@operation ~]# python
Python 2.7.5 (default, Aug  4 2017, 00:39:18)
[GCC 4.8.5 20150623 (Red Hat 4.8.5-16)] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>>


# 安装Python2的pip
[root@operation ~]# yum install epel-release -y
[root@operation ~]# yum -y install python-pip
 
# 安装完成后不是最新的pip版本要进行升级
[root@operation ~]# pip install --upgrade pip
 
# 测试
[root@operation ~]# pip -V(大写V)
pip 18.1 from /usr/lib/python2.7/site-packages/pip (python 2.7)
 
# 现在可以使用pip进行对Python2 进行安装Python包了
# 第一种方法：
[root@operation ~]# pip install 包名
 
# 第二种方法：
[root@operation ~]# python -m pip install pymongo  (安装Python2的包)
 
# 若是安装的Python3
[root@operation ~]# python3 -m pip install pymongo (安装Python3的包)
```



## 二、安装Python3

----

**安装依赖关系**

```bash
[root@operation ~]# yum install zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gcc make
 
注：不能忽略相关包，我之前就没有安装readline-devel导致执行python模式无法使用键盘的上下左右键；
```
**下载源码包**

```bash
[root@operation ~]# wget --no-check-certificate https://www.python.org/ftp/python/3.7.3/Python-3.7.3.tgz
 
注：如果没有wget命令可以使用 yum -y install wget 安装
注：我这里安装的是3.7.3的Python版本 如果想要安装其他的版本可以直接修改版本号
```
**解压、编译、安装**

```bash
# 解压
[root@operation ~]# tar -xvJf Python-3.7.3.tgz
 
# 编译
[root@operation ~]# cd Python-3.7.3
[root@operation Python-3.7.3]# ./configure prefix=/usr/local/python3
 
# 安装
[root@operation Python-3.7.3]# make && make install
 
注：没有报错及安装成功，如果报错可以看看是不是一些依赖包没有安装 自行解决不了可以留言评论或者直接联系我
```

**设置软连接**

```bash
# 安装完成还是不可以直接在终端输入python3 进入编译器的，我们需要设置软链接
[root@operation Python-3.7.3]# ln -s /usr/local/python3/bin/python3 /usr/bin/python3
 
# 这样直接执行Python3 就可以进入Python3版本的解释器了
[root@operation Python-3.7.3]# python3
Python 3.7.3 (default, Oct 12 2018, 12:02:11)
[GCC 4.8.5 20150623 (Red Hat 4.8.5-28)] on linux
Type "help", "copyright", "credits" or "license" for more information.
>>>
```

**配置Python3的pip**

```bash
# 设置完python执行后 python3的pip还是不能的用的，也是需要设置的软链接才可以的，在python3的解压目录下是有pip3的命令的
[root@operation Python-3.7.3]# cd /usr/local/python3/bin/
[root@operation bin]# ll pip*
-rwxr-xr-x 1 root root 232 10月 12 12:08 pip
-rwxr-xr-x 1 root root 232 10月 12 12:08 pip3
-rwxr-xr-x 1 root root 232 10月 12 12:08 pip3.6
 
# 我们需要做个软链接即可
[root@operation bin]# ln -s /usr/local/python3/bin/pip3 /usr/bin/pip3
 
# 安装完成后不是最新的pip3版本要进行升级
[root@operation ~]# pip3 install --upgrade pip
```

**测试**

```bash
# 测试
[root@operation bin]# pip3 -V
pip 18.1 from /usr/local/python3/lib/python3.6/site-packages/pip (python 3.6)
 
# 使用
[root@operation bin]# pip3 install 包名
 
# 或者
[root@operation bin]# python3 -m pip install 包名
```



## 三、安装TAB补全的解释器(ipython)

-----

**安装(我这里安装双版本的)**

```bash

# 安装Python2的ipython
# 第一种方法
[root@operation ~]# pip install ipython
# 第二种方法
[root@operation ~]# python -m pip install ipython
 
# 安装Python3的ipython
# 第一种方法
[root@operation ~]# pip3 install ipython
# 第二种方法
[root@operation ~]# python3 -m pip install ipython
 
注：安装无报错安装成功
```

**双版本设置软链接**

```bash

# 因为是安装了Python的双版本而且安装的包名都叫 ipython 所有我们执行ipython的时候使用的是安装的python2的版本，我们要使用双版本就要使用软链接
 
[root@operation ~]# ipython
Python 2.7.5 (default, Aug  4 2017, 00:39:18)
Type "copyright", "credits" or "license" for more information.
 
IPython 5.8.0 -- An enhanced Interactive Python.
?         -> Introduction and overview of IPython's features.
%quickref -> Quick reference.
help      -> Python's own help system.
object?   -> Details about 'object', use 'object??' for extra details.
 
In [1]:
 
 
 
# 设置Python3的ipython 使用软链接
[root@operation ~]# ln -s /usr/local/python3/bin/ipython /usr/bin/ipython3
```

**测试**

```bash
# Python2的ipython
[root@operation ~]# ipython
Python 2.7.5 (default, Aug  4 2017, 00:39:18)
Type "copyright", "credits" or "license" for more information.
 
IPython 5.8.0 -- An enhanced Interactive Python.
?         -> Introduction and overview of IPython's features.
%quickref -> Quick reference.
help      -> Python's own help system.
object?   -> Details about 'object', use 'object??' for extra details.
 
In [1]:
 
 
# Python3的ipython
[root@operation ~]# ipython3
Python 3.7.3 (default, Oct 12 2018, 12:02:11)
Type 'copyright', 'credits' or 'license' for more information
IPython 7.0.1 -- An enhanced Interactive Python. Type '?' for help.
 
In [1]: 
```



参考

[centos7 python2和python3共存](https://live.staticflickr.com/65535/47757192572_2fc399f84a_z.jpg)

