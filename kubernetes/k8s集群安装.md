# k8s集群安装



## 准备工作

```
1.关闭防火墙，关闭selinux(生产环境按需关闭或打开)
2.同步服务器时间，选择公网ntpd服务器或者自建ntpd服务器
3.关闭swap分区  
4.集群所有节点主机可以相互解析
5.master对node节点ssh互信
6.配置系统内核参数使流过网桥的流量也进入iptables/netfilter框架(如果报错，提示没有文件   modprobe br_netfilter  添加此模块)
  echo -e 'net.bridge.bridge-nf-call-iptables = 1 \nnet.bridge.bridge-nf-call-ip6tables = 1' >> /etc/sysctl.conf  && sysctl -p
```



## 移除旧版本

>yum remove kubernetes-client 
>yum remove kubernetes-node
>yum clean packages
>yum update
>
>yum install -y kubelet kubeadm kubectl
>systemctl enable kubelet && systemctl start kubelet

## 配置各节点阿里K8S YUM源
```
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
 
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
 
EOF
 
yum -y install epel-release
 
yum clean all
 
yum makecache
```
## 安装指定版本

> sudo yum install -y kubernetes-cni-0.6.0 kubelet-1.12.0 kubeadm-1.12.0 kubectl-1.12.0



## 锁定软件版本阻止升级的方法

> vim /etc/yum.conf

在最后加上：

>exclude=docker*
>exclude=kube*



## **启动相关服务并设置开机自启**

>systemctl daemon-reload 
systemctl enable docker && systemctl restart docker
systemctl enable kubelet && systemctl restart kubelet



## 更新相关依赖
### 自动生成和安装requirements.txt依赖

生成requirements.txt文件
>pip freeze > requirements.txt

安装requirements.txt依赖

>pip install -r requirements.txt

### 安装或更新 idna

> pip install idna

或 

> pip3 install idna





## 其它

[python2和python3并存](https://www.cnblogs.com/zhujingzhi/p/9778043.html)

