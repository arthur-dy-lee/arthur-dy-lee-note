## 业务场景

在web应用开发中我们经常会遇到这样的场景：一个请求任务，我们需要去查多个库，并对查询到的数据做处理，此时如果采用同步的方式去查，往往会导致请求响应时间过慢。比如：两个查询任务task1，task2，task1查询数据要花2s，处理数据要花1s；task2查询数据花5s，处理数据花2s，那一次请求的时间是2+1+5+2=10s。而如果我们用异步的方式，则能减少请求响应的时间。
 而利用异步的方式，常常子任务还未执行完，主线程就已经结束了，导致数据不能很好的返回到前端，所以主线程必须保证所有的子任务执行结束后才能退出。
 接下来我讲讨论各种异步方式来处理这种业务场景的方式。

## 方式一：利用java多线程工具Future.get()获取数据

```java
public class TestFuture {
    // 任务一执行2s
    public static class Task1 implements Callable {
        public Object call() throws Exception {
            System.out.println("task1 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task1");
            Thread.sleep(2000);
            System.out.println("task1 ending ...");
            return lists;
        }
    }
    // 任务一执行5s
    public static class Task2 implements Callable {
        public Object call() throws Exception {
            System.out.println("task2 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task2");
            Thread.sleep(5000);
            System.out.println("task2 ending ...");
            return lists;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        int cpuNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Future<List> future1 = executor.submit(new Task1());
        Future<List> future2 = executor.submit(new Task2());
        // 获取任务一和任务二的数据 进行处理
        List<String> lists1 = future1.get();
        List<String> lists2 = future2.get();
        // ===》分析点：
        dealTask1Data(lists1);
        dealTask2Data(lists2);
        System.out.println("task ending");
        Long time = System.currentTimeMillis() - start;
        System.out.println("执行任务所花的时间：" + time + "s");
    }

    // 处理任务1数据 处理1s
    public static void dealTask1Data(List<String> lists) throws InterruptedException {
        System.out.println("deal task1 data ...");
        Thread.sleep(1000);
    }

    // 处理任务2数据 处理2s
    public static void dealTask2Data(List<String> lists) throws InterruptedException {
        System.out.println("deal task2 data ...");
        Thread.sleep(2000);
    }
}
```

执行结果

```
task1 starting ...
task2 starting ...
task1 ending ...
task2 ending ...
deal task1 data ...
deal task2 data ...
task ending
执行任务所花的时间：8009s
```

结果分析：
 查看源码===》标注处，利用future1.get(),future2.get()获取数据，需要等到future1和future2所有的数据返回后，主线程才能继续往下执行，所以执行到future2.get()的时间需要5s，而后处理task1数据1s，处理task2数据2s，执行时间为5+1+2 = 8s。

## 方式二： 利用CountDownLatch让主线程等待子线程任务结束

```java
public class TestCountDownLatch {
    // 任务一执行2s
    public static class Task1 implements Callable {
        private CountDownLatch countDownLatch;
        public Task1(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
        public Object call() throws Exception {
            System.out.println("task1 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task1");
            Thread.sleep(2000);
            System.out.println("task1 ending ...");
            // 对任务一的数据进行处理
            dealTask1Data(lists);
            // 任务一结束 对countDownLatch计数器--
            countDownLatch.countDown();
            return lists;
        }
        // 处理任务1数据
        public void dealTask1Data(List<String> lists) throws InterruptedException {
            System.out.println("deal task1 data ...");
            Thread.sleep(1000);
        }
    }

    // 任务一执行5s
    public static class Task2 implements Callable {
        private CountDownLatch countDownLatch;
        public Task2(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
        public Object call() throws Exception {
            System.out.println("task2 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task2");
            Thread.sleep(5000);
            System.out.println("task2 ending ...");
            // 对任务二的数据进行处理
            dealTask2Data(lists);
            // 任务二结束 对countDownLatch计数器--
            countDownLatch.countDown();
            return lists;
        }
        // 处理任务2数据
        public static void dealTask2Data(List<String> lists) throws InterruptedException {
            System.out.println("deal task2 data ...");
            Thread.sleep(2000);
        }
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        int cpuNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        executor.submit(new Task1(countDownLatch));
        executor.submit(new Task2(countDownLatch));
        // 等countDownLatch == 0时，主线程结束 10s超时，自动结束，如果任务没超过10s，也得等10s
        // countDownLatch.await(10000, TimeUnit.MILLISECONDS);
        // ===> 等到countDownLatch计数器为0，才往下执行
        countDownLatch.await();
        System.out.println("task ending ...");
        long time = System.currentTimeMillis() - start;
        System.out.println("执行任务所花的时间：" + time + "s");
    }
}
```

执行结果：

```
task1 starting ...
task2 starting ...
task1 ending ...
deal task1 data ...
task2 ending ...
deal task2 data ...
task ending ...
执行任务所花的时间：7031s
```

结果分析：
 将任务查询到的数据处理放到每个线程里处理，然后利用CountDownLatch作为计数器，开始给CountDownLatch设置任务数，在每个线程执行完毕之后，计数器减一，在===》标注点，主线程会等countDownLatch计数器为0的时候才会继续往下执行。因为上面代码将数据处理放到了每个线程中，每个线程是并发执行的，所以任务执行时间是5+2=7s。

## 方式三：利用CyclicBarrier让主线程等待子线程

```java
public class TestCyclicBarrier {
    // 任务一执行2s
    public static class Task1 implements Callable {
        private CyclicBarrier cyclicBarrier;
        public Task1(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }
        public Object call() throws Exception {
            System.out.println("task1 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task1");
            Thread.sleep(2000);
            System.out.println("task1 ending ...");
            // 对任务一的数据进行处理
            dealTask1Data(lists);
            // 任务一结束 对countDownLatch计数器--
            cyclicBarrier.await();
            return lists;
        }
        // 处理任务1数据
        public void dealTask1Data(List<String> lists) throws InterruptedException {
            System.out.println("deal task1 data ...");
            Thread.sleep(1000);
        }
    }
    // 任务一执行2s
    public static class Task2 implements Callable {
        private CyclicBarrier cyclicBarrier;
        public Task2(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }
        public Object call() throws Exception {
            System.out.println("task2 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task2");
            Thread.sleep(5000);
            System.out.println("task2 ending ...");
            // 对任务二的数据进行处理
            dealTask2Data(lists);
            // 任务二结束 对countDownLatch计数器--
            cyclicBarrier.await();
            return lists;
        }
        // 处理任务2数据
        public static void dealTask2Data(List<String> lists) throws InterruptedException {
            System.out.println("deal task2 data ...");
            Thread.sleep(2000);
        }
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException, BrokenBarrierException, TimeoutException {
        long start = System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        int cpuNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        executor.submit(new Task1(cyclicBarrier));
        executor.submit(new Task2(cyclicBarrier));
        // 等countDownLatch == 0时，主线程结束 3s超时，超时会报异常
        // cyclicBarrier.await(3000, TimeUnit.MILLISECONDS);
        // ===》
        cyclicBarrier.await();
        System.out.println("task ending ...");
        long time = System.currentTimeMillis() - start;
        System.out.println("执行任务所花的时间：" + time + "s");
    }
}
```

执行结果：

```
task1 starting ...
task2 starting ...
task1 ending ...
deal task1 data ...
task2 ending ...
deal task2 data ...
task ending ...
执行任务所花的时间：7022s
```

结果分析：
 当代码执行到===》标注点的时候，cyclicBarrier.await()会看task1和task2的代码是否也执行到了cyclicBarrier.await()，如果有任务没有执行到，则会继续等待，只有3个任务同时执行到了cyclicBarrier.await()任务才会继续往下执行。

## CountDownLatch与CyclicBarrier的区别

javadoc的解释：

- CountDownLatch:
   A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
   一个线程(或者多个)， 只有另外N个线程完成某个事情之后才能继续往下执行。（即只有计数器为0的时候，才能继续往下执行）
- CyclicBarrier :
   A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point.
   N个线程相互等待，只有所有的线程都执行到了barrier点，所有线程才能继续往下执行，否则所有线程都必须等待。

## 方式四:利用CompletionService

```java
public class TestCompletion {
    // 任务一执行2s
    public static class Task1 implements Callable {
        public Object call() throws Exception {
            System.out.println("task1 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task1");
            Thread.sleep(5000);
            System.out.println("task1 ending ...");
            return lists;
        }
    }
    // 任务一执行3s
    public static class Task2 implements Callable {
        public Object call() throws Exception {
            System.out.println("task2 starting ...");
            List<String> lists = new ArrayList<String>();
            lists.add("task2");
            Thread.sleep(3000);
            System.out.println("task2 ending ...");
            return lists;
        }
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException, BrokenBarrierException, TimeoutException {
        long start = System.currentTimeMillis();
        int cpuNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        CompletionService<List<String>> completionService = new ExecutorCompletionService<List<String>>(executor);
        completionService.submit(new Task1());
        completionService.submit(new Task2());
        // 能做到先返回任务,结果就先输出
        try {
            for (int i = 0; i < 2; i++) {
//                Future<List<String>> result = completionService.take();
//                System.out.println("hello world");
//                System.out.println(result.get());
                Future<List<String>> result2 = completionService.poll(5000, TimeUnit.MILLISECONDS);
                System.out.println(result2.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw e;
        }

        System.out.println("task ending ...");
        long time = System.currentTimeMillis() - start;
        System.out.println("执行任务所花的时间：" + time + "s");
    }
}
```

CompletionService将Executor和BlockingQueue的功能融合在一起.可以将Callable任务提交给它来执行,然后使用类似与队列操作的take和poll等方法来获得已完成的结果,而这些结果会在完成时将被封装为Future.ExecutorCompletionService实现了CompletionService,并将计算部分委托给一个Executor.

ExecutorCompletionService的实现非常简单.在构造函数中创建一个BlockingQueue来保存计算完成的结果.当计算完成时,调用Future-Task中的done方法.当提交某个任务时,该任务将首先包装为一个QueueingFuture,这是FutureTask的一个子类,然后再改写子类的done方法,并将结果放入BlockingQueue中.take和poll方法委托给了BlockingQueue,这些方法会在得出结果之前阻塞.

```java
private class QueueingFuture<V> extends FutureTask<V> {

    QueueingFuture(Callable<V> c){super(c);}

    QueueingFuture(Runnable t, V r) {super(t, r);}

    protected void done() {

        completionQueue.add(this);        

    }

}
```

结果:

```
task1 starting ...
task2 starting ...
task2 ending ...
[task2]
task1 ending ...
[task1]
task ending ...
执行任务所花的时间：5015s
```

多个ExecutorCompletionservice可以共享一个Executor,因此可以创建一个特定计算私有,又能共享一个公共Executor的ExecutorCompletionService.因此,CompletionService的作用就相当于一组计算的句柄.这与Future作为单个计算句柄是非常类似的.通过记录提交CompletionService的任务数量,并计算出已经获得的已完成结果的数量.通过记录提交给CompletionService的任务数量,并计算出已经获得的已完成结果的数量,即使使用一个共享的Executor,也能知道已经获得了所有任务结果的时间.

 
