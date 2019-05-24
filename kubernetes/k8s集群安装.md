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



关闭selinux

vi /etc/selinux/config



### 移除旧版本

>yum remove kubernetes-client 
>yum remove kubernetes-node
>yum clean packages
>yum update
>
>yum install -y kubelet kubeadm kubectl
>systemctl enable kubelet && systemctl start kubelet

### 配置各节点阿里K8S YUM源
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


### 锁定软件版本阻止升级的方法

> vim /etc/yum.conf

在最后加上：

>exclude=docker*
>exclude=kube*



### 启动相关服务并设置开机自启

>systemctl daemon-reload 
systemctl enable docker && systemctl restart docker
systemctl enable kubelet && systemctl restart kubelet



### 更新相关依赖
### 自动生成和安装requirements.txt依赖

生成requirements.txt文件
>pip freeze > requirements.txt

安装requirements.txt依赖

>pip install -r requirements.txt

### 安装或更新 idna

> pip install idna

或 

> pip3 install idna



### 其它

[python2和python3并存](https://www.cnblogs.com/zhujingzhi/p/9778043.html)



##  二、 安装

##  Ansible ， [详情](<https://gitee.com/paincupid/kubespray>)

```shell
# Install dependencies from ``requirements.txt``
sudo pip install -r requirements.txt

# Copy ``inventory/sample`` as ``inventory/mycluster``
cp -rfp inventory/sample inventory/mycluster

# Update Ansible inventory file with inventory builder
declare -a IPS=(10.10.1.3 10.10.1.4 10.10.1.5)
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