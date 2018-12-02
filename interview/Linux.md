



## 如何查看当前占用CPU或内存最多的K个进程

一、可以使用以下命令查使用内存最多的K个进程

方法1：

```
ps -aux | sort -k4nr | head -K
```

如果是10个进程，K=10，如果是最高的三个，K=3

**说明：**ps -aux中（a指代all——所有的进程，u指代userid——执行该进程的用户id，x指代显示所有程序，不以终端机来区分）

​        ps -aux的输出格式如下：

```
USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root         1  0.0  0.0  19352  1308 ?        Ss   Jul29   0:00 /sbin/init
root         2  0.0  0.0      0     0 ?        S    Jul29   0:00 [kthreadd]
root         3  0.0  0.0      0     0 ?        S    Jul29   0:11 [migration/0]
```

​     sort -k4nr中（k代表从第几个位置开始，后面的数字4即是其开始位置，结束位置如果没有，则默认到最后；n指代numberic sort，根据其数值排序；r指代reverse，这里是指反向比较结果，输出时默认从小到大，反向后从大到小。）。本例中，可以看到%MEM在第4个位置，根据%MEM的数值进行由大到小的排序。

​     head -K（K指代行数，即输出前几位的结果）

​     |为管道符号，将查询出的结果导到下面的命令中进行下一步的操作。

方法2：top （然后按下M，注意大写）

二、可以使用下面命令查使用CPU最多的K个进程

方法1：

```
ps -aux | sort -k3nr | head -K
```

方法2：top （然后按下P，注意大写）







