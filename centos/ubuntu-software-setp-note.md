

# ubuntu软件安装笔记


## 授权
### 文件夹
> sudo chmod 777 folderName

### 切换成root
> su

## 基础命令

### 清屏
ctrl + l

### 退出 shell
ctrl + d

### 新建文件夹
> mkdir folderName

### 显示隐藏文件和文件夹命令行方式
> ls -a

### 重名名文件（夹）
mv命令，mv 原文件（夹）名 新文件（夹）名

> mv testdir hw

## System Settings won't start
> sudo apt-get remove unity-control-center

> sudo apt autoremove

> sudo apt-get install unity-control-center

## vi
基本上vi可以分为三种状态，分别是命令模式（command mode）、插入模式（Insert mode）和底行模式（last line mode）

在「命令行模式（command mode）」下按一下字母「i」就可以进入「插入模式（Insert mode）」，这时候你就可以开始输入文字了。

「ESC」键转到「命令行模式（command mode）」

: wq (输入「wq」，存盘并退出vi)

: q! (输入q!， 不存盘强制退出vi)

### 移动光标
「h」、「j」、「k」、「l」，分别控制光标左、下、上、右移一格。

按数字「0」：移到文章的开头。

按「G」：移动到文章的最后。

按「$」：移动到光标所在行的“行尾”。

按「^」：移动到光标所在行的“行首”

### 插入模式
　　按「i」切换进入插入模式「insert mode」，按“i”进入插入模式后是从光标当前位置开始输入文件；

　　按「a」进入插入模式后，是从目前光标所在位置的下一个位置开始输入文字；

　　按「o」进入插入模式后，是插入新的一行，从行首开始输入文字。

### 删除文字

「x」：每按一次，删除光标所在位置的“后面”一个字符。

「#x」：例如，「6x」表示删除光标所在位置的“后面”6个字符。

「X」：大写的X，每按一次，删除光标所在位置的“前面”一个字符

「#X」：例如，「20X」表示删除光标所在位置的“前面”20个字符

「dd」：删除光标所在行。

「#dd」：从光标所在行开始删除#行

### 复制
「yw」：将光标所在之处到字尾的字符复制到缓冲区中。

「#yw」：复制#个字到缓冲区

「yy」：复制光标所在行到缓冲区。

「#yy」：例如，「6yy」表示拷贝从光标所在的该行“往下数”6行文字。

「p」：将缓冲区内的字符贴到光标所在位置。注意：所有与“y”有关的复制命令都必须与“p”配合才能完成复制与粘贴功能。

### 查找字符
「/关键字」：先按「/」键，再输入您想寻找的字符，如果第一次找的关键字不是您想要的，可以一直按「n」会往后寻找到您要的关键字为止。

「?关键字」：先按「?」键，再输入您想寻找的字符，如果第一次找的关键字不是您想要的，可以一直按「n」会往前寻找到您要的关键字为止。

### 保存文件
「w」：在冒号输入字母「w」就可以将文件保存起来

## 安装jdk8
### 下载tar.gz
### 解压
```xml
lxh@ubuntu:~$ mkdir -p /usr/lib/jvm  
lxh@ubuntu:~$ sudo mv jdk-8u11-linux-i586.tar.gz /usr/lib/jvm  
lxh@ubuntu:~$ cd /usr/lib/jvm  
lxh@ubuntu:~$ sudo tar xzvf jdk-8u11-linux-i586.tar.gz  
sudo mv jdk1.8.0_111 java8
```
### 设置环境变量
> vi ~/.bashrc

在文件最后加入
```xml
export JAVA_HOME=/usr/lib/jvm/java8  
export JRE_HOME=${JAVA_HOME}/jre  
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib  
export PATH=${JAVA_HOME}/bin:$PATH
```
保存退出，并通过命令使脚本生效：
> :wq

### 配置默认JDK版本
如果系统没有默认安装就不用下面的命令。

在有的系统中会预装OpenJDK，系统默认使用的是这个，而不是刚才装的。所以这一步是通知系统使用Oracle的JDK，非OpenJDK。

```
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-8/bin/java 300  
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-8/bin/javac 300  
sudo update-alternatives --config java  
```

### 测试验证
> java -version

## 输入法fcitx

```xml
//先卸载IBUS输入法
killall ibus-daemon
sudo apt-get purge ibus ibus-gtk ibus-gtk3 ibus-pinyin* ibus-sunpinyin ibus-table python-ibus
rm -rf ~/.config/ibus
//安装fcitx输入法
sudo add-apt-repository ppa:fcitx-team/nightly
sudo apt-get update
sudo apt install fcitx-table-wbpy fcitx-config-gtk
```
其他输入法
```xml
//拼音：
fcitx-sogoupinyin
fcitx-pinyin、fcitx-sunpinyin、fcitx-googlepinyin，
//五笔：
fcitx-table、fcitx-table-wubi、fcitx-table-wbpy（五笔拼音混合）
```
命令切换为Fcitx输入法框架
>im-config -n fcitx

重启机器
>sudo systemctl restart lightdm.service

## Ubuntu中bash自动补全忽略大小写
大多数人在使用 Bash 时，都会对其进行改造，因为默认的设置真的好难用～

编辑 ~/.inputrc 文件设置 (实测Ubuntu14是   /etc/.inputrc   文件)


文件末尾添加如下代码:
```xml
# do not show hidden files in the list
set match-hidden-files off

# auto complete ignoring case
set show-all-if-ambiguous on
set completion-ignore-case on

"\e[A": history-search-backward
"\e[B": history-search-forward
```
解释:

>show-all-if-ambiguous : 默认情况下，按下两次 <tab> 才会出现提示，现在只需要一次了。

>match-hidden-files : 不显示隐藏文件，特别是当你在 Home 目录时，你会觉得眼前好干净。

>completion-ignore-case : 在自动补全时忽略大小写

>history-search-* : 输入某个命令的一部分时，按上下箭头，会匹配关于这个这命令最近的使用历史。

## 安装chrome
先卸载
>   apt-get autoremove google-chrome-stable


## 修改时区
sudo dpkg-reconfigure tzdata

## 修改root密码
sudo passwd 你的用户名

## 退出root用户，退回当前登陆用户
exit

## find: ‘/run/user/1000/gvfs’: Permission denied
> sudo umount /run/user/1000/gvfs

> sudo rm -rf /run/user/1000/gvfs



## 修改ip地址

### 网关地址方法
> ip route show

or

>route -n or netstat -rn

```xml
$ route -n  
Kernel IP routing table  
Destination     Gateway         Genmask         Flags Metric Ref    Use Iface  
0.0.0.0         192.168.217.2   0.0.0.0         UG    100    0        0 eth0  
192.168.122.0   0.0.0.0         255.255.255.0   U     0      0        0 virbr0  
192.168.217.0   0.0.0.0         255.255.255.0   U     0      0        0 eth0  
```
eth0 网关地址为 192.168.217.2
我的是ens33
### 进入修改
> sudo vi /etc/network/interfaces

or

> sudo gedit /etc/network/interfaces

并用下面的行来替换有关eth0的行:# The primary network interface
```xml
#auto lo
#iface lo inet loopback
auto ens33
iface ens33 inet static
address 192.168.192.88
gateway 192.168.192.1
netmask 223.5.5.5
broadcast 192.168.192.255
```

### 配置dns服务器
> sudo gedit /etc/resolvconf/resolv.conf.d/head

or

> sudo  gedit /etc/resolvconf/resolvconf.d/tail

打开后添加阿里DNS
```
nameserver 223.6.6.6
nameserver 223.5.5.5
```

## 重启下resolvconf程序的方法
> sudo /etc/init.d/networking restart

如果上面命令无法令ubuntu重启网络，则使用下面命令：

sudo ifdown ens33 && sudo ifup ens33

> sudo ifdown ens33 && sudo ifup ens33


## 查找命令
### find
/usr/lib 是要查找的目标目录，如果查找所有，用/就可以了。
```xml
find /usr/lib -name libmysqlclient.so.20
```
#### locate
```
locate libmysqlclient.so.20
```

### whereis 文件名
```
特点:快速,但是是模糊查找,例如 找 #whereis mysql 它会把mysql,mysql.ini,mysql.*所在的目录都找出来.
```

## 删除ln软链接链接

### 显示所有软链接 ls -il
```xml
[root@rekfan.com test]# ls -il
总计  0
1491138 -rw-r–r– 1 root root 48 07-14 14:17 file1
1491139 -rw-r–r– 2  root root 0 07-14 14:17 file2
1491139 -rw-r–r– 2 root root 0 07-14 14:17  file2hand
```
### 先建立软链接 ln -s file1  file1soft
```xml
#建立file1和file1soft软连接
[root@rekfan.com test]# ln -s file1  file1soft
[root@rekfan.com test]# ls -il
总计 0
1491138 -rw-r–r– 1 root  root 48 07-14 14:17 file1
1491140 lrwxrwxrwx 1 root root 5 07-14 14:24  file1soft -> file1
1491139 -rw-r–r– 2 root root 0 07-14 14:17  file2
1491139 -rw-r–r– 2 root root 0 07-14 14:17 file2hand
```
### 删除软链接 rm -rf file1soft
```xml
#删除软连接
[root@rekfan.com test]# rm -rf file1soft
[root@rekfan.com test]#  ls -il
总计 0
1491138 -rw-r–r– 1 root root 0 07-14 14:17 file1
1491139  -rw-r–r– 2 root root 0 07-14 14:17 file2
1491139 -rw-r–r– 2 root root 0 07-14  14:17 file2hand
```
rm -rf /lib/x86_64-linux-gnu/libcrypto.so.1.0.0



## 查看已安装的软件
> sudo dpkg --list

> sudo dpkg --list mysql*

## 卸载软件
> sudo apt-get -- purge remove msyql-server

## 删除没用的
> sudo apt autoremove


## 查看ubuntu版本
sudo uname --m

如果显示i686,你安装了32位操作系统
如果显示 x86_64，你安装了64位操作系统

## 重启命令
1、reboot
2、shutdown -r now 立刻重启(root用户使用)
3、shutdown -r 10 过10分钟自动重启(root用户使用)
4、shutdown -r 20:35 在时间为20:35时候重启(root用户使用)
如果是通过shutdown命令设置重启的话，可以用shutdown -c命令取消重启

## 关机命令
1、halt   立刻关机
2、poweroff 立刻关机
3、shutdown -h now 立刻关机(root用户使用)
4、shutdown -h 10 10分钟后自动关机



## 删除文件以及文件夹

>  sudo rm -r apache-maven-3.2.5





