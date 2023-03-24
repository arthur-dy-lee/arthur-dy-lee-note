## 一、raft简介

### raft涉及到的名词

Raft算法将Server划分为3种状态，或者也可以称作角色：

- Leader
  负责Client交互和log复制，同一时刻系统中最多存在1个。
- Follower
  被动响应请求RPC，从不主动发起请求RPC。
- Candidate
  一种临时的角色，只存在于leader的选举阶段，某个节点想要变成leader，那么就发起投票请求，同时自己变成candidate。如果选举成功，则变为candidate，否则退回为follower

## 二、raft原理

#### Raft算法流程
Raft 算法中服务器节点之间通信使用远程过程调用（RPCs），并且基本的一致性算法只需要两种类型的 RPCs，为了在服务器之间传输快照增加了第三种 RPC。
RPC有三种：
RequestVote RPC：候选人在选举期间发起
AppendEntries RPC：领导人发起的一种心跳机制，复制日志也在该命令中完成
InstallSnapshot RPC: 领导者使用该RPC来发送快照给太落后的追随者

#### 领导选取

（1）follower增加当前的term，转变为candidate。
（2）candidate投票给自己，并发送RequestVote RPC给集群中的其他服务器。
（3）收到RequestVote的服务器，在同一term中只会按照先到先得投票给至多一个candidate。且只会投票给log至少和自身一样新的candidate。

#### 日志复制

日志复制（Log Replication）主要作用是用于保证节点的一致性，这阶段所做的操作也是为了保证一致性与高可用性。
当Leader选举出来后便开始负责客户端的请求，所有事务（更新操作）请求都必须先经过Leader处理，日志复制（Log Replication）就是为了保证执行相同的操作序列所做的工作。

在Raft中当接收到客户端的日志（事务请求）后先把该日志追加到本地的Log中，然后通过heartbeat把该Entry同步给其他Follower，Follower接收到日志后记录日志然后向Leader发送ACK，当Leader收到大多数（n/2+1）Follower的ACK信息后将该日志设置为已提交并追加到本地磁盘中，通知客户端并在下个heartbeat中Leader将通知所有的Follower将该日志存储在自己的本地磁盘中。

#### 安全和成员变化。

## 三、

## 四、raft应用

## 五、raft和zk区别

## 六、raft开源项目







#### Raft

Raft 集群中的成员分三种角色：
Leader
Follower
Condidate

##### Raft 的选举过程

Raft 协议在集群初始状态下是没有 Leader 的, 集群中所有成员均是 Follower，在选举开始期间所有 Follower 均可参与选举，这时所有 Follower 的角色均转变为 Condidate, Leader 由集群中所有的 Condidate 投票选出，最后获得投票最多的 Condidate 获胜，其角色转变为 Leader 并开始其任期，其余落败的 Condidate 角色转变为 Follower 开始服从 Leader 领导。

有一种意外的情况会选不出 Leader 就是所有 Condidate 均投票给自己，这样无法决出票数多的一方，Raft 算法为了解决这个问题引入了北洋时期袁世凯获选大总统的谋略，即选不出 Leader 不罢休，直到选出为止，一轮选不出 Leader，便令所有 Condidate 随机 sleep（Raft 论文称为 timeout）一段时间，然后马上开始新一轮的选举，这里的随机 sleep 就起了很关键的因素，第一个从 sleap 状态恢复过来的 Condidate 会向所有 Condidate 发出投票给我的申请，这时还没有苏醒的 Condidate 就只能投票给已经苏醒的 Condidate ，因此可以有效解决 Condiadte 均投票给自己的故障，便可快速的决出 Leader。

在raft算法中，比较谁的数据最新有2个参考指标，任期和logIndex，任期大的节点，数据一定最新，任期一样的话，就要比较该任期内谁的MaxLogIndex最大了。
先判断term，再判断日志是否是最新的。至少任期以及日志记录，不比自己旧，才会投票给你。所以过时的节点不会得到大多数的投票。

任期的作用：

 - 不同的服务器节点观察到的任期转换的次数可能不同，在某些情况下，一个服务器节点可能没有看到 leader 选举过程或者甚至整个任期全程。
 - 任期在 Raft 算法中充当逻辑时钟的作用，这使得服务器节点可以发现一些过期的信息比如过时的 leader 。
 - 每一个服务器节点存储一个当前任期号，该编号随着时间单调递增。
 - 服务器之间通信的时候会交换当前任期号；
 - 如果一个服务器的当前任期号比其他的小，该服务器会将自己的任期号更新为较大的那个值。
 - 如果一个 candidate 或者 leader 发现自己的任期号过期了，它会立即回到 follower 状态。（所以说老leader如果发生了网络分区，后来接收到新leader的心跳的时候，比拼完任期之后，会自动变成follower。
 - 如果一个节点接收到一个包含过期的任期号的请求，它会直接拒绝这个请求。
 - 如果选出了 Leader，那么就会 一直维持这个 Term，直到下一次选举。

1. 当一个 candidate 获得集群中过半服务器节点针对同一个任期的投票，它就赢得了这次选举并成为 leader 。
   对于同一个任期，每个服务器节点只会投给一个 candidate ，按照先来先服务（first-come-first-served）的原则）。
   要求获得过半投票的规则确保了最多只有一个 candidate 赢得此次选举。
   一旦 candidate 赢得选举，就立即成为 leader 。然后它会向其他的服务器节点发送心跳消息来确定自己的地位并阻止新的选举。


2. 在等待投票期间，candidate 可能会收到另一个声称自己是 leader 的服务器节点发来的 AppendEntries RPC 。
   如果这个 leader 的任期号（包含在RPC中）不小于 candidate 当前的任期号，那么 candidate 会承认该 leader 的合法地位并回到 follower 状态。
   如果 RPC 中的任期号比自己的小，那么 candidate 就会拒绝这次的 RPC 并且继续保持 candidate 状态。

3. candidate 既没有赢得选举也没有输：如果有多个 follower 同时成为 candidate ，那么选票可能会被瓜分以至于没有 candidate 赢得过半的投票。
   当这种情况发生时，每一个候选人都会超时，然后通过增加当前任期号来开始一轮新的选举。然而，如果没有其他机制的话，该情况可能会无限重复。


##### Raft 的数据一致性策略

Raft 协议强依赖 Leader 节点来确保集群数据一致性。即 client 发送过来的数据均先到达 Leader 节点，Leader 接收到数据后，先将数据标记为 uncommitted 状态，随后 Leader 开始向所有 Follower 复制数据并等待响应，在获得集群中大于 N/2 个 Follower 的已成功接收数据完毕的响应后，Leader 将数据的状态标记为 committed，随后向 client 发送数据已接收确认，在向 client 发送出已数据接收后，再向所有 Follower 节点发送通知表明该数据状态为committed。

##### 投票期间重新收到leader心跳

candidate之所以会发起选举，是因为没有收到leader的心跳，但是在选举期间又重新收到心跳会如何？
论文中描述，当重新受到leader的心跳时会判断term，至少不能比自己小，也就是说，即使是因为自己网络原因没有收到心跳而发起投票，也不会终止这次投票，因为老leader的term比现在的要小，自己是自增了一次的。
但是如果在投票等待期间，已经有新的leader产生，并且接收到leader的 appending的RPC时，candidate会放弃投票，因为term不小于当前candidate，说明这个leader不是老leader，要么和自己是同一个term的leader，要么比自己更新term的leader。
所以理论上存在某一个follower的节点因为网路延迟而发起leader申请，并且还有可能成功顶替leader的可能性，即使leader的功能正常，是这个follower自己的网络突然发生了延迟。

##### 心跳

在 Raft 里面，如果选出来一个 Leader，Leader 会定期给 Follower 发送心跳，这个定期的时间我们通常叫做 heartbeat timeout，如果 Follower 在 election timeout 的时间里都没收到 Leader 的消息，就开始新一轮的选举。Heartbeat timeout 的时间要比 election timeout 小很多，譬如 election timeout 如果是 10s，那么 heartbeat timeout 可能就是 2s 或者 3s。

##### 脑裂

集群脑裂的一致性处理，多发于双机房的跨机房模式的集群。假设一个 5 节点的 Raft 集群，其中三个节点在 A 机房，Leader 节点也在 A 机房，两个节点在 B 机房。突然 A、B 两个机房之间因其他故障无法通讯，那么此时 B 机房中的 2 个Follower 因为失去与 Leader 的联系，均转变自身角色为 Condidate。根据 Leader 选举机制，B 机房中产生了一个新的 Leader，这就发生了脑裂即存在 A 机房中的老 Leader 的集群与B机房新 Leader 的集群。Raft 针对这种情况的处理方式是老的 Leader 集群虽然剩下三个节点，但是 Leader 对数据的处理过程还是在按原来 5 个节点进行处理，所以老的 Leader 接收到的数据，在向其他 4 个节点复制数据，由于无法获取超过 N/2 个 Follower 节点的复制完毕数据响应（因为无法连接到 B 机房中的 2个节点），所以 client 在向老 Leader 发送的数据请求均无法成功写入，而 client 向B机房新 Leader 发送的数据，因为是新成立的集群，所以可以成功写入数据，在A、B两个机房恢复网络通讯后，A 机房中的所有节点包括老 Leader 再以 Follower 角色接入这个集群，并同步新 Leader 中的数据，完成数据一致性处理。

##### ZAB和Raft对比

  1、定义
ZAB通过事务ID，区别不同的纪元epoch，事务ID共64位，前32位在一个纪元中相同
Raft定义了term任期和logIndex。

2、投票
ZK中的每个server，在某个electionEpoch轮次内，可以投多次票，只要遇到更大的票就更新，ZK先比较epoch，epoch大的当选；epoch相同，再比较事务id zxid；都相同比较myid，myid大的优先。
Raft中的每个server在某个term轮次内只能投一次票，哪个candidate先请求投票谁就可能先获得投票,Raft通过candidate设置不同的超时时间，来快速解决这个问题

3、过半
ZAB协议，只有当过半节点提交了事务，才会给客户端事务提交的回应; 而Raft协议，leader提交了事务，并且收到过半follower对准备完成事务的ack后，自身节点提交事务，至于过半数节点提交事务这件事，是在之后通信过程中渐渐完成的。

4、primary-backup system or state machine system：raft是state machine system，zab是primary-backup system

![](/Users/lidongyue/codes/GitHub/arthur-dy-lee-note/interview/pics/state machine replication vs primary backup system.jpg)

从这个图可以看出primary backup system 的做法是, 当有client 请求到达的时候, primary 会立刻将这个请求apply 到自己的 state machine, 然后将这个结果发送给自己的backup, 而不是这个client 的请求(比如这里就是将y=2 发送给backup, 而不是发送y++ 这个操作), 然后这里发送给自己多个backups 的时候是通过一致性协议来进行发送.

在primary backup system 里面, primary 在接收到client 的请求以后, 会立刻apply 到自己的state machine, 然后计算出这个结果, 写到自己的log, 以及发送给所有的backups. 而在state machine replication 系统里面, 是直接将这个操作发送给各个replication的.

5、恢复方向：raft单向，仅从leader到follower补齐log；zab双向，leader需要从follower接收数据来生成initial history。

zab 的解决方法就是当recovery 的时候, 将leader 上面的所有日志都拉去过来, 然后丢弃自己state machine 的内容, 直接使用新leader state machine 的内容

在 "Vive La Diffe ́rence:Paxos vs. Viewstamped Replication vs. Zab" 这个论文里面, state machine replication 也称作active replication, 而primary-backup system 也称作passive replication, 这样更加的形象, 也就是 state machine replication 是主动去同步数据, 通过达成一致性协议来返回给client, 而primary-backup system 是primary 做了所有了事情, 只是通过一致性协议把数据保存在backups 里面

https://zhuanlan.zhihu.com/p/30856272 

https://www.zhihu.com/question/28242561