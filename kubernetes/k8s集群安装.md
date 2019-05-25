# k8s集群安装



## 一、准备工作

```
1.关闭防火墙，关闭selinux(生产环境按需关闭或打开)
2.同步服务器时间，选择公网ntpd服务器或者自建ntpd服务器
3.关闭swap分区  
4.集群所有节点主机可以相互解析
5.master对node节点ssh互信
6.配置系统内核参数使流过网桥的流量也进入iptables/netfilter框架(如果报错，提示没有文件   modprobe br_netfilter  添加此模块)
  echo -e 'net.bridge.bridge-nf-call-iptables = 1 \nnet.bridge.bridge-nf-call-ip6tables = 1' >> /etc/sysctl.conf  && sysctl -p
```



### 1.1 关闭selinux等
执行完selinux要重启

关闭防火墙、selinux、swap等操作 直接执行以下脚本
```bash
# 所有主机：基本系统配置
 
# 关闭Selinux/firewalld
systemctl stop firewalld
systemctl disable firewalld
setenforce 0
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
 
# 关闭交换分区
swapoff -a
yes | cp /etc/fstab /etc/fstab_bak
cat /etc/fstab_bak |grep -v swap > /etc/fstab
 
# 设置网桥包经IPTables，core文件生成路径
echo """
vm.swappiness = 0
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
""" > /etc/sysctl.conf
sysctl -p
 
# 同步时间
yum install -y ntpdate
ntpdate -u ntp.api.bz
```


### 1.2 移除旧版本

>yum remove kubernetes-client 
>yum remove kubernetes-node
>yum clean packages
>yum update
>
>yum install -y kubelet kubeadm kubectl
>systemctl enable kubelet && systemctl start kubelet



### 1.3锁定软件版本阻止升级的方法

> vim /etc/yum.conf

在最后加上：

>exclude=docker*
>exclude=kube*

### 1.4 安装python和python3

[python2和python3并存](https://www.cnblogs.com/zhujingzhi/p/9778043.html)

### 确定docker版本和k8s安装的版本是支持的
1、查询安装过的包
```bash
yum list installed | grep docker
containerd.io.x86_64                1.2.5-3.1.el7              @docker-ce-stable
docker-ce.x86_64                    3:18.09.5-3.el7            @docker-ce-stable
docker-ce-cli.x86_64                1:18.09.5-3.el7            @docker-ce-stable
```
2、删除安装的软件包
```bash
yum -y remove docker-engine.x86_64 docker-ce.x86_64 docker-ce-cli.x86_64  
```
3、删除镜像/容器等
```bash
rm -rf /var/lib/docker
```
4、安装docker
```bash
yum install yum-utils device-mapper-persistent-data lvm2
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
##查看可安装的docker版本，还要支持对应的k8s版本
yum list docker-ce --showduplicates|grep "^doc"|sort -r
## Install Docker CE.
yum update -y && yum install docker-ce-18.09.5
## Create /etc/docker directory.
mkdir /etc/docker

# Setup daemon.
cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2",
  "storage-opts": [
    "overlay2.override_kernel_check=true"
  ]
}
EOF

mkdir -p /etc/systemd/system/docker.service.d

# Restart Docker
systemctl daemon-reload
systemctl restart docker
```

### 1.6 启动相关服务并设置开机自启

>systemctl daemon-reload 
systemctl enable docker && systemctl restart docker
systemctl enable kubelet && systemctl restart kubelet






##  二、 安装

##  Ansible ， [详情](<https://gitee.com/paincupid/kubespray>)

```shell
# Install dependencies from ``requirements.txt``
sudo pip install -r requirements.txt

# Copy ``inventory/sample`` as ``inventory/mycluster``
cp -rfp inventory/sample inventory/mycluster

# Update Ansible inventory file with inventory builder
declare -a IPS=(192.168.3.11 192.168.3.12 192.168.3.13)
CONFIG_FILE=inventory/mycluster/hosts.yml python3 contrib/inventory_builder/inventory.py ${IPS[@]}

# Review and change parameters under ``inventory/mycluster/group_vars``
cat inventory/mycluster/group_vars/all/all.yml
cat inventory/mycluster/group_vars/k8s-cluster/k8s-cluster.yml

# Deploy Kubespray with Ansible Playbook - run the playbook as root
# The option `-b` is required, as for example writing SSL keys in /etc/,
# installing packages and interacting with various systemd daemons.
# Without -b the playbook will fail to run!
ansible-playbook -i inventory/mycluster/hosts.yml --become --become-user=root cluster.yml
```



## 十、遇到的问题

### 10.1 delegate_to' is not a valid attribute for a TaskInclude

修改 [lib/ansible/config/base.yml](https://github.com/ansible/ansible/commit/509e92ef72dbce56cdce413452be2b6268c5bf6c#diff-fd24ad93fbc32f454761746c1ac908f2) 文件： INVALID_TASK_ATTRIBUTE_FAILED为false

```yml
type: boolean
INVALID_TASK_ATTRIBUTE_FAILED:
  name: Controls whether invalid attributes for a task result in errors instead of warnings
  default: false
  description: If 'true', invalid attributes for a task will result in errors instead of warnings
  default: True
  description: If 'false', invalid attributes for a task will result in warnings instead of errors
  type: boolean
  env:
    - name: ANSIBLE_INVALID_TASK_ATTRIBUTE_FAILED
```



### 10.2 ERROR: Cannot uninstall 'python-ldap'. It is a distutils installed project and thus we cannot accurately determine which files belong to it which would lead to only a partial uninstall.

> pip install --ignore-installed python-ldap

### 10.3

```shell
fatal: [node2]: FAILED! => {"attempts": 4, "changed": true, "cmd": ["/usr/bin/docker", "pull", "k8s.gcr.io/cluster-proportional-autoscaler-amd64:1.4.0"], "delta": "0:00:15.443377", "end": "2019-05-24 19:41:45.997584", "msg": "non-zero return code", "rc": 1, "start": "2019-05-24 19:41:30.554207", "stderr": "Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)", "stderr_lines": ["Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)"], "stdout": "", "stdout_lines": []}
fatal: [node1]: FAILED! => {"attempts": 4, "changed": true, "cmd": ["/usr/bin/docker", "pull", "k8s.gcr.io/cluster-proportional-autoscaler-amd64:1.4.0"], "delta": "0:00:15.462422", "end": "2019-05-24 19:41:53.254354", "msg": "non-zero return code", "rc": 1, "start": "2019-05-24 19:41:37.791932", "stderr": "Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)", "stderr_lines": ["Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)"], "stdout": "", "stdout_lines": []}
```







----------



declare -a IPS=(192.168.8.6 192.168.8.7 192.168.8.8)
CONFIG_FILE=inventory/mycluster/hosts.yml python3 contrib/inventory_builder/inventory.py ${IPS[@]}

ansible -i inventory -m ping -u root 192.168.8.6

arthur6     ansible_ssh_host=192.168.8.6   ansible_ssh_user=root   ansible_ssh_pass=eee  ip=192.168.8.6   mask=/24
arthur7     ansible_ssh_host=192.168.8.7   ansible_ssh_user=root   ansible_ssh_pass=eee  ip=192.168.8.7   mask=/24
arthur8     ansible_ssh_host=192.168.8.8   ansible_ssh_user=root   ansible_ssh_pass=eee  ip=192.168.8.8   mask=/24

authorized_keys



